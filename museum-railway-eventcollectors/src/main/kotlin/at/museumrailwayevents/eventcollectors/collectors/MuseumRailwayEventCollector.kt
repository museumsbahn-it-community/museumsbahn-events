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
) : EventCollector {

    protected fun createEvent(
        eventName: String,
        startDate: OffsetDateTime,
        additionalData: MutableMap<String, String>,
        locationId: String = this.locationId,
        url: String = this.url,
    ): Event {
        additionalData[CommonKeys.OPERATOR_ID] = operatorId
        additionalData[CommonKeys.LOCATION_ID] = locationId
        additionalData[SemanticKeys.SOURCES] = url
        return Event(eventName, startDate, additionalData)
    }
}