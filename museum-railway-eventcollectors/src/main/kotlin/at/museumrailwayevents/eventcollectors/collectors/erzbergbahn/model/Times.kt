package at.museumrailwayevents.eventcollectors.collectors.erzbergbahn.model

import kotlinx.serialization.Serializable

@Serializable
data class Times(
    var days: Map<String, Day>,
    var range: Range,
)

@Serializable
data class Day(
    var times: Map<String, TimeEntry>,
)

@Serializable
data class TimeEntry(
    var capacity: Int? = null,
    var availableQuantity: Int? = null,
    var variationIds: ArrayList<Int> = arrayListOf()
)

@Serializable
data class Range(
    var from: String? = null,
    var to: String? = null
)