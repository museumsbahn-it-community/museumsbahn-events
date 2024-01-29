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
        private const val OPERATOR_LOCATION_INDEX = 5
        private const val MUSEUM_LOCATION_INDEX = 9

        private val logger = KotlinLogging.logger {}
    }

    private var cachedMuseumOperators: List<MuseumOperator> = loadMuseumOperators();
    private var cachedMuseumLocations: List<MuseumLocation> = loadMuseumLocations();

    val museumOperators get() = cachedMuseumOperators
    val museumLocations get() = cachedMuseumLocations

    fun reloadData() {
        cachedMuseumOperators = loadMuseumOperators()
        cachedMuseumLocations = loadMuseumLocations()
    }

    private fun loadMuseumOperators(): List<MuseumOperator> {
        val classLoader = javaClass.getClassLoader()
        val resource = classLoader.getResource(MUSEUM_OPERATORS_FILE)?.openStream()
        requireNotNull(resource) {"could not load museum operators resource"}
        val reader = resource.bufferedReader()
        reader.readLine() // skip header
        val operators = reader.lineSequence()
            .filter { it.isNotBlank() }.map { line ->
                parseMuseumOperator(line)
            }.toList()

        return operators
    }

    private fun loadMuseumLocations(): List<MuseumLocation> {
        val classLoader = javaClass.getClassLoader()
        val resource = classLoader.getResource(MUSEUM_LOCATIONS_FILE)?.openStream()
        requireNotNull(resource) {"could not load museum locations resource"}
        val rows: List<List<String>> = csvReader().readAll(resource)
        val rowsWithoutHeader = rows.subList(1, rows.size)

        val locations = rowsWithoutHeader.map { line ->
                parseMuseumLocation(line)
            }.toList()

        return locations
    }

    private fun parseMuseumOperator(line: String): MuseumOperator {
        val data = line.split(",")

        val location = parseLocation(data, OPERATOR_LOCATION_INDEX)

        return MuseumOperator(
            name = data[0],
            identifier = data[1],
            webUrl = data[2],
            description = data[3],
            imageName = data[4],
            location
        )
    }

    private fun parseMuseumLocation(data: List<String>): MuseumLocation {
        val location = parseLocation(data, MUSEUM_LOCATION_INDEX)

        var type = MuseumType.Museum
        val museumTypeString = data[1]
        try {
            type = MuseumType.valueOf(museumTypeString)
        } catch (e: Exception) {
            logger.warn("could not parse museum type: $museumTypeString, allowed values: ${MuseumType.entries.toString()}")
        }

        return MuseumLocation(
            name = data[0],
            type = type,
            operatorId = data[2],
            locationId = data[3],
            webUrl = data[4],
            description = data[5],
            imageName = data[6],
            eventCollectorType = data[7],
            eventCollectionNotPossibleReason = data[8],
            location
        )
    }

    private fun parseLocation(data: List<String>, locationIndex: Int) = Location(
        country = "Österreich",
        state = data[locationIndex],
        street = data[locationIndex + 1],
        zipCode = data[locationIndex + 2],
        city = data[locationIndex + 3],
        lat = data[locationIndex + 4].trim().toFloat(),
        lon = data[locationIndex + 5].trim().toFloat(),
    )
}