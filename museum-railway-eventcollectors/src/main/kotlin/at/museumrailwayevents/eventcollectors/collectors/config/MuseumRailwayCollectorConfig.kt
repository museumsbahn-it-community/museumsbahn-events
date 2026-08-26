package at.museumrailwayevents.eventcollectors.collectors.config

import base.boudicca.api.eventcollector.config.EventCollectorBaseConfig

/**
 * Intermediate config shared by all museum railway collectors, adding the common
 * operator/location identity fields and source URL on top of the vendored
 * [EventCollectorBaseConfig]'s `name`.
 *
 * All properties are defaulted. Spring's `Binder` (see the vendored `EventCollector.configure()`)
 * throws if a required constructor parameter cannot be bound from yaml, so every property here -
 * and on every subclass - must stay optional. Collectors that don't (yet) externalize their values
 * into `application.yml` simply override these as Kotlin constants instead; the config defaults
 * are then never used.
 */
open class MuseumRailwayCollectorConfig(
    name: String,
    open val operatorId: String = "",
    open val locationId: String = "",
    open val locationName: String = "",
    open val sourceUrl: String = "",
    open val tags: List<String> = emptyList(),
) : EventCollectorBaseConfig(name)
