package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event

abstract class SternHafferlCollector(
    val jsoupCrawler: JsoupCrawler,
    val locomotiveType: String,
    operatorId: String,
    locationId: String,
    url: String,
    tags: List<String> = emptyList(),
    locationName: String
) : MuseumRailwayEventCollector(
    operatorId, locationId, url, tags, locationName
) {

    protected fun collectEventsFromPage(url: String): List<Event> {
        val document = jsoupCrawler.getDocument(url)
        val dateStrings = document.select("div#event-date-list li").eachText()
        val dates = dateStrings.map { DateParser.parseDate(it) }
        val name = "$locationName - ${document.select("h1").text()}"
        val description = document.select("h1 ~ p").eachText().joinToString("\n")
        val imageUrl = document.select("img").attr("src")
        val events = mutableListOf<Event>()

        dates.forEach { date ->
            events.add(
                createEvent(
                    name,
                    date,
                    mutableMapOf(
                        SemanticKeys.CATEGORY to CATEGORY_MUSEUM_TRAIN,
                        SemanticKeys.REGISTRATION to Registration.TICKET,
                        SemanticKeys.DESCRIPTION to description,
                        CommonKeys.LOCOMOTIVE_TYPE to Tags.LOCOMOTIVE_TYPE_TRAM,
                        SemanticKeys.RECURRENCE_TYPE to RecurrenceType.RARELY,
                        SemanticKeys.TAGS to TAGS_MUSEUM_EVENT.toTagsValue(),
                        SemanticKeys.PICTURE_URL to imageUrl,
                    ),
                )
            )
        }

        return events
    }

}