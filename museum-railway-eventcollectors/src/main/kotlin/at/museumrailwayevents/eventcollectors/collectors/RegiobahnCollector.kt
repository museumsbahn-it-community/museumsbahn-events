package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.keepLineBreaks
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event

class RegiobahnCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    "regiobahn",
    "regiobahn",
    "https://regiobahn.at/ausflugsfahrten/",
    locationName = "Regiobahn"
) {
    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(sourceUrl)
        val eventLinks = document.select("div.tourmaster-tour-grid-inner a").map { it.attr("href") }.toSet()
        val pictureUrls = document.select("div.tourmaster-tour-thumbnail img")
            .map { it.attr("src") }
            .filterNot { it.startsWith("data") } // for some reason there are some svgs in there, so we have to get rid of them

        return eventLinks.mapIndexed { index, eventUrl ->
            try {
                parseEvent(eventUrl, pictureUrls, index)
            } catch(ex: Exception) {
                println("error parsing event: $eventUrl")
                null
            }
        }.filterNotNull()
    }

    private fun parseEvent(eventUrl: String, pictureUrls: List<String>, index: Int): Event {
        val eventDocument = jsoupCrawler.getDocument(eventUrl)

        val dateText = eventDocument.select("div.tourmaster-single-header-title-datum").text()
        val startDate = DateParser.parseDate(dateText)
        val title = eventDocument.select("h1").text()
        val description = eventDocument.keepLineBreaks().select("div.gdlr-core-pbf-element:contains(DETAILS ZUR VERANSTALTUNG) + div.gdlr-core-pbf-element").text()
        //val pictureUrl = pictureUrls[index]
        val pictureUrl = eventDocument
            .select("div.tourmaster-single-tour-content-wrap div.gdlr-core-gallery-list img")
            .attr("data-lazy-src")

        val event = createStructuredEvent(
            title,
            startDate,
            eventUrl,
            mutableMapOf(
                SemanticKeys.DESCRIPTION_TEXT_PROPERTY.getKey(language = "de") to description,
                SemanticKeys.CATEGORY_PROPERTY.getKey() to MuseumEventsCategory.SPECIAL_TRIP.jsonValue,
                SemanticKeys.PICTURE_URL_PROPERTY.getKey() to (pictureUrl ?: ""), // ugly hack needed, but can realistically not be null
                SemanticKeys.REGISTRATION_PROPERTY.getKey() to MuseumEventRegistration.PRE_SALES_ONLY.jsonValue,
                SemanticKeys.RECURRENCE_TYPE_PROPERTY.getKey() to RecurrenceType.RARELY,
            )
        ).toBuilder()

        return event.build().toFlatEvent()
    }


    override fun getName(): String = "Regiobahn Collector"
}