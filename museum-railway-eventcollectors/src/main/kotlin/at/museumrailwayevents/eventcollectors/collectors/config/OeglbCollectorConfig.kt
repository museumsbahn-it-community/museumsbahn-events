package at.museumrailwayevents.eventcollectors.collectors.config

import at.museumrailwayevents.model.conventions.VehicleType

/**
 * Config for collectors built on the "Oeglb" (lokalbahnen.at) page pattern shared by
 * Ybbstalbahn and Höllentalbahn: a regular "Fahrplan" page plus a "Sonderfahrten" page,
 * both reachable from a common [baseUrl]. Collectors that don't need any fields beyond
 * these can bind this config class directly instead of declaring their own subclass.
 */
open class OeglbCollectorConfig(
    name: String,
    operatorId: String = "",
    locationId: String = "",
    locationName: String = "",
    sourceUrl: String = "",
    tags: List<String> = emptyList(),
    open val baseUrl: String = "",
    open val fahrplanUrls: List<String> = emptyList(),
    open val sonderfahrtenUrl: String = "",
    // defaults to UNKNOWN rather than guessing a vehicle - collectors should supply this from yaml
    open val locomotiveType: String = VehicleType.UNKNOWN,
) : MuseumRailwayCollectorConfig(name, operatorId, locationId, locationName, sourceUrl, tags)
