package at.museumrailwayevents.service

import at.museumrailwayevents.model.Location
import at.museumrailwayevents.model.MuseumLocation
import at.museumrailwayevents.model.MuseumOperator
import at.museumrailwayevents.model.MuseumType
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DataLoaderService {
    companion object {
        private const val MUSEUM_OPERATORS_FILE = "operators.csv"
        private const val MUSEUM_LOCATIONS_FILE = "locations.csv"
        private val csvReader = csvReader {
            skipEmptyLine = true
        }

        private val logger = KotlinLogging.logger {}
    }

    private var cachedMuseumOperators: List<MuseumOperator> = loadMuseumOperators();
    private var cachedMuseumLocations: List<MuseumLocation> = loadMuseumLocations();

    val museumOperators get() = cachedMuseumOperators
    val museumLocations get() = cachedMuseumLocations

    init {
        reloadData()
    }

    final fun reloadData() {
        cachedMuseumOperators = loadMuseumOperators()
        cachedMuseumLocations = loadMuseumLocations()
    }

    private fun loadMuseumOperators(): List<MuseumOperator> {
        val classLoader = javaClass.getClassLoader()
        val resource = classLoader.getResource(MUSEUM_OPERATORS_FILE)?.openStream()
        requireNotNull(resource) { "could not load museum operators resource" }
        val lines: List<Map<String, String>> = csvReader.readAllWithHeader(resource)
        val operators = lines.mapNotNull { line ->
            parseMuseumOperator(line)
        }

        return operators
    }

    private fun loadMuseumLocations(): List<MuseumLocation> {
        val classLoader = javaClass.getClassLoader()
        val resource = classLoader.getResource(MUSEUM_LOCATIONS_FILE)?.openStream()
        requireNotNull(resource) { "could not load museum locations resource" }
        val rows: List<Map<String, String>> = csvReader.readAllWithHeader(resource)

        val locations = rows.mapNotNull { line ->
            parseMuseumLocation(line)
        }

        return locations
    }

    private fun parseMuseumOperator(line: Map<String, String>): MuseumOperator? {
        try {
            val location = parseLocation(line)

            return MuseumOperator(
                    name = line["name"]!!,
                    identifier = line["identifier"]!!,
                    webUrl = line["webUrl"]!!,
                    description = line["description"]!!,
                    imageName = line["imageName"]!!,
                    location
            )
        } catch (ex: Exception) {
            logger.error { "could not parse museum operator: \n${line}" }
            logger.error { "reason: \n${ex.stackTraceToString()}" }
            return null
        }
    }

    private fun parseMuseumLocation(line: Map<String, String>): MuseumLocation? {
        try {
            val location = parseLocation(line)

            var type = MuseumType.Museum
            val museumTypeString = line["type"]!!
            try {
                type = MuseumType.valueOf(museumTypeString)
            } catch (e: Exception) {
                logger.warn("could not parse museum type: $museumTypeString, allowed values: ${MuseumType.entries.toString()}")
            }

            return MuseumLocation(
                    name = line["name"]!!,
                    type = type,
                    operatorId = line["operatorId"]!!,
                    locationId = line["locationId"]!!,
                    webUrl = line["webUrl"]!!,
                    description = line["description"]!!,
                    imageName = line["imageName"]!!,
                    eventCollectorType = line["eventCollectorType"]!!,
                    eventCollectionComment = line["eventCollectionComment"]!!,
                    location
            )
        } catch (ex: Exception) {
            logger.error { "could not parse museum location: \n${line}" }
            logger.error { "reason: ${ex.stackTraceToString()}" }
            return null
        }
    }

    private fun parseLocation(data: Map<String, String>): Location {
        val latString = data["lat"]
        val lonString = data["lon"]
        val lat = if (!latString.isNullOrBlank()) {
            latString.trim().toFloat()
        } else {
            null
        }
        val lon = if (!lonString.isNullOrBlank()) {
            lonString.trim().toFloat()
        } else {null}
        return Location(
                country = "Österreich",
                state = data["state"]!!,
                street = data["street"]!!,
                zipCode = data["zipCode"],
                city = data["city"],
                lat = lat,
                lon = lon,
        )
    }
}