package at.museumrailwayevents.service

import at.museumrailwayevents.model.*
import io.github.oshai.kotlinlogging.KotlinLogging

object ParsingUtils {

    private val logger = KotlinLogging.logger {}

    fun parseMuseumOperator(line: Map<String, String>): MuseumOperator? {
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

    fun parseMuseumLocation(line: Map<String, String>, locationImageData: List<ImageSpec>): MuseumLocation? {
        try {
            val location = parseLocation(line)

            var type = MuseumType.MUSEUM
            val museumTypeString = line["type"]!!
            try {
                type = MuseumType.valueOf(museumTypeString)
            } catch (e: Exception) {
                logger.warn("could not parse museum type: $museumTypeString, allowed values: ${MuseumType.entries}")
            }

            return MuseumLocation(
                name = line["officialName"]!!,
                shortName = line["shortName"]!!,
                type = type,
                operatorId = line["operatorId"]!!,
                locationId = line["locationId"]!!,
                webUrl = line["webUrl"]!!,
                description = line["description"],
                eventCollectorType = line["eventCollectorType"],
                eventCollectionComment = line["eventCollectionComment"],
                googleMapsUrl = line["googleMapsUrl"],
                eventListUrl = line["eventListUrl"],
                mapyczUrl = line["mapyczUrl"],
                geoJsonUrl = line["geoJsonUrl"],
                openingHoursUrl = line["openingHoursUrl"],
                location = location,
                images = locationImageData
            )
        } catch (ex: Exception) {
            logger.error { "could not parse museum location: \n${line}" }
            logger.error { "reason: ${ex.stackTraceToString()}" }
            return null
        }
    }

    fun parseLocation(data: Map<String, String>): Location {
        val latString = data["lat"]
        val lonString = data["lon"]
        val lat = if (!latString.isNullOrBlank()) {
            latString.trim().toFloat()
        } else {
            null
        }
        val lon = if (!lonString.isNullOrBlank()) {
            lonString.trim().toFloat()
        } else {
            null
        }
        return Location(
            country = data["country"]!!,
            state = data["state"]!!,
            street = data["street"]!!,
            zipCode = data["zipCode"],
            city = data["city"],
            lat = lat,
            lon = lon,
        )
    }
}