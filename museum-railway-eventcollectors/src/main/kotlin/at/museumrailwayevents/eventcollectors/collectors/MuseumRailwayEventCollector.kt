package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.model.conventions.CommonKeys
import base.boudicca.SemanticKeys
import base.boudicca.api.eventcollector.EventCollector
import base.boudicca.model.Event
import java.time.OffsetDateTime

abstract class MuseumRailwayEventCollector(
    protected val operatorId: String,
    protected val locationId: String,
    protected val url: String,
    protected val tags: List<String> = emptyList(),
    protected val locationName: String
) : EventCollector {

    protected fun createEvent(
        eventName: String,
        startDate: OffsetDateTime,
        additionalData: MutableMap<String, String>,
        locationId: String = this.locationId,
        url: String = this.url, // TODO change to list
    ): Event {
        additionalData[CommonKeys.OPERATOR_ID] = operatorId
        additionalData[CommonKeys.LOCATION_ID] = locationId
        additionalData[SemanticKeys.SOURCES] = url

        if (additionalData.containsKey(SemanticKeys.PICTURE_URL) && !additionalData.containsKey(SemanticKeys.PICTURE_ALT_TEXT)) {
            additionalData[SemanticKeys.PICTURE_ALT_TEXT] = "Veranstaltungsbild von $locationName"
        }

        if (additionalData.containsKey(SemanticKeys.PICTURE_URL) && !additionalData.containsKey(SemanticKeys.PICTURE_COPYRIGHT)) {
            additionalData[SemanticKeys.PICTURE_COPYRIGHT] = "© $locationName"
        }

        return Event(eventName, startDate, additionalData)
    }
}