package at.museumrailwayevents.service

import at.museumrailwayevents.config.GoogleConfig
import at.museumrailwayevents.model.GoogleDriveImageSpec
import at.museumrailwayevents.model.ImageSpec
import at.museumrailwayevents.model.MuseumLocation
import at.museumrailwayevents.model.MuseumOperator
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.io.FileInputStream

private const val OPERATORS_SHEET_NAME = "Operators"
private const val LOCATIONS_SHEET_NAME = "Locations"

private const val APPLICATION_NAME = "museumsbahn-events.at backend"

@Service
class GoogleDataLoaderService(val googleConfig: GoogleConfig, val driveDownloadService: GoogleDriveDownloadService) {
    private val logger = KotlinLogging.logger {}
    private val gsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val credentials = createCredentials()
    private val spreadsheetId = googleConfig.spreadsheetId
    private val sheetsService = Sheets.Builder(httpTransport, gsonFactory, HttpCredentialsAdapter(credentials))
        .setApplicationName(APPLICATION_NAME)
        .build()

    private lateinit var cachedMuseumOperators: List<MuseumOperator>
    private lateinit var cachedMuseumLocations: List<MuseumLocation>
    private var museumLocationImageData = mutableMapOf<String, List<GoogleDriveImageSpec>>()

    val museumOperators get() = cachedMuseumOperators
    val museumLocations get() = cachedMuseumLocations

    init {
        updateData()
    }

    private fun createCredentials(): GoogleCredentials {
        var credentials: GoogleCredentials =
            GoogleCredentials.fromStream(FileInputStream(googleConfig.credentialsPath))
        credentials = credentials.createScoped(SheetsScopes.SPREADSHEETS_READONLY)
        return credentials
    }

    private fun loadSheetRange(sheetName: String): List<List<String>>? {
        // Get the sheet's properties
        val spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute()
        val sheet = spreadsheet.sheets.find { it.properties.title == sheetName }
            ?: throw IllegalArgumentException("Sheet not found")

        // Get the sheet's dimensions
        val gridProperties = sheet.properties.gridProperties
        val rowCount = gridProperties.rowCount
        val columnCount = gridProperties.columnCount

        // Construct the range string
        val range = "$sheetName!A1:${columnToLetter(columnCount)}$rowCount"

        // Fetch the data
        val response = sheetsService.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()

        return response.getValues()?.map { line -> line.map { it.toString() } }
    }

    private fun columnToLetter(column: Int): String {
        var tempColumn = column
        val result = StringBuilder()
        while (tempColumn > 0) {
            val remainder = (tempColumn - 1) % 26
            result.insert(0, (remainder + 'A'.code).toChar())
            tempColumn = (tempColumn - 1) / 26
        }
        return result.toString()
    }

    fun updateData() {
        logger.info { "starting data update" }
        cachedMuseumOperators = loadMuseumOperators(OPERATORS_SHEET_NAME)
        logger.info { "museum operators updated" }

        cachedMuseumLocations = loadMuseumLocations(LOCATIONS_SHEET_NAME)
        logger.info { "museum locations updated" }
    }

    private fun loadMuseumOperators(sheetId: String): List<MuseumOperator> {
        return mapDataFromSheet(sheetId) { data ->
            ParsingUtils.parseMuseumOperator(data)
        }
    }

    private fun loadMuseumLocations(sheetId: String): List<MuseumLocation> {
        return mapDataFromSheet(sheetId) { data ->
            if (data["published"]?.lowercase() == "true") {
                val locationId = data["locationId"]
                requireNotNull(locationId)
                val locationImageData = try {
                    val imageData = data["images"]
                    if (!imageData.isNullOrBlank()) {
                        val parsedImageData = Json.decodeFromString<List<GoogleDriveImageSpec>>(imageData)
                        museumLocationImageData[locationId] = parsedImageData
                        parsedImageData.mapIndexed { index, spec ->
                            ImageSpec(
                                "/api/location/${locationId}/image/${index}",
                                spec.copyright,
                                spec.alt
                            )
                        }
                    } else {
                        emptyList()
                    }
                } catch (ex: Exception) {
                    logger.warn { "exception: ${ex.message}" }
                    emptyList()
                }

                ParsingUtils.parseMuseumLocation(data, locationImageData)
            } else {
                null
            }
        }
    }

    private fun <T> mapDataFromSheet(
        sheetName: String,
        mapDataToObject: (data: Map<String, String>) -> T?
    ): List<T> {
        val data = loadSheetRange(sheetName)
        requireNotNull(data) { "could not load google spreadsheet document" }
        val keys = data.first()
        val dataWithoutHeader = data.subList(1, data.size - 1)
        val mappedData = dataWithoutHeader.mapIndexed { lineIdx, lineData ->
            val mappedLine = lineData.mapIndexed { cellIdx, cellData ->
                if (cellIdx < keys.size) {
                    keys[cellIdx] to cellData
                } else {
                    null
                }
            }.filterNotNull().toMap()
            if (mappedLine["published"]?.lowercase() != "true") {
                return@mapIndexed null
            }

            mapDataToObject(mappedLine)
        }.filterNotNull()
        return mappedData
    }

    // TODO: cache calls and add option to reload caches
    fun loadImageForLocation(locationId: String, imageIndex: Int, size: Int? = null): ByteArray? {
        val locationImageData = museumLocationImageData[locationId] ?: return null
        val imageSpec = locationImageData[imageIndex]
        return driveDownloadService.downloadImage(imageSpec.googleDriveId, size)
    }

    fun reloadData() {
        this.updateData()
    }

}