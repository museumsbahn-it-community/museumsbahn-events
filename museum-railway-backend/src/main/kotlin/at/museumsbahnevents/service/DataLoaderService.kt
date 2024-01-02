package at.museumsbahnevents.service

import at.museumsbahnevents.api.model.Location
import at.museumsbahnevents.api.model.MuseumLocation
import at.museumsbahnevents.api.model.MuseumOperator
import at.museumsbahnevents.api.model.MuseumType
import jakarta.enterprise.context.ApplicationScoped
import mu.KotlinLogging
import java.io.File


@ApplicationScoped
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
        val resource = classLoader.getResource(MUSEUM_OPERATORS_FILE)
        val reader = File(resource.file).bufferedReader()
        reader.readLine() // skip header
        val operators = reader.lineSequence()
            .filter { it.isNotBlank() }.map { line ->
                parseMuseumOperator(line)
            }.toList()

        return operators
    }

    private fun loadMuseumLocations(): List<MuseumLocation> {
        val classLoader = javaClass.getClassLoader()
        val resource = classLoader.getResource(MUSEUM_LOCATIONS_FILE)
        val reader = File(resource.file).bufferedReader()
        reader.readLine() // skip header
        val locations = reader.lineSequence()
            .filter { it.isNotBlank() }.map { line ->
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

    private fun parseMuseumLocation(line: String): MuseumLocation {
        val data = line.split(",")

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