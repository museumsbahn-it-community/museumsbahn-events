package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.model.conventions.CommonKeys
import base.boudicca.model.structured.Key
import base.boudicca.SemanticKeys
import base.boudicca.TextProperty
import base.boudicca.api.eventcollector.EventCollector
import base.boudicca.format.UrlUtils
import base.boudicca.model.Event
import base.boudicca.model.structured.StructuredEvent
import java.time.OffsetDateTime

/**
 * @param sourceUrl: link to the page from where the event was collected, should link to the list page of the association
 */
abstract class MuseumRailwayEventCollector(
    protected val operatorId: String,
    protected val locationId: String,
    protected val sourceUrl: String,
    protected val tags: List<String> = emptyList(),
    protected val locationName: String
) : EventCollector {

    @Deprecated("use createStructuredEvent instead")
    protected fun createEvent(
        eventName: String,
        startDate: OffsetDateTime,
        eventUrl: String,
        additionalData: MutableMap<String, String>,
        locationId: String = this.locationId,
        sourceUrl: String = this.sourceUrl,
    ): Event {
        // this is a temporary method to make the old collectors compatible with the new property system
        // it should be remove once it's no longer used
        val additionalDataWithKeys = additionalData.mapKeys {
            Key.builder(it.key).build()
        }.toMutableMap()

        return createStructuredEvent(eventName, startDate, eventUrl, additionalDataWithKeys, locationId, sourceUrl).toFlatEvent()
    }

    protected fun createStructuredEvent(
        eventName: String,
        startDate: OffsetDateTime,
        eventUrl: String,
        additionalData: MutableMap<Key, String>,
        locationId: String = this.locationId,
        sourceUrl: String = this.sourceUrl,
    ): StructuredEvent {
        val pictureUrlKey = SemanticKeys.PICTURE_URL_PROPERTY.getKey()
        val altTextKey = SemanticKeys.PICTURE_ALT_TEXT_PROPERTY.getKey()
        val copyrightKey = SemanticKeys.PICTURE_COPYRIGHT_PROPERTY.getKey()

        val builder = StructuredEvent(eventName, startDate, additionalData)
            .toBuilder()
            .withProperty(SemanticKeys.URL_PROPERTY, UrlUtils.parse(eventUrl))
            .withProperty(SemanticKeys.SOURCES_PROPERTY, listOf(sourceUrl))
            .withProperty(TextProperty(CommonKeys.OPERATOR_ID), operatorId)
            .withProperty(TextProperty(CommonKeys.LOCATION_ID), locationId)

        if (additionalData.containsKey(pictureUrlKey) && !additionalData.containsKey(altTextKey)) {
            builder.withProperty(SemanticKeys.PICTURE_ALT_TEXT_PROPERTY,"Veranstaltungsbild von $locationName")
        }

        if (additionalData.containsKey(pictureUrlKey) && !additionalData.containsKey(copyrightKey)) {
            builder.withProperty(SemanticKeys.PICTURE_COPYRIGHT_PROPERTY, "© $locationName")
        }

        return builder.build()
    }
}