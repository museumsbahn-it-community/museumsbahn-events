package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.CATEGORY_MUSEUM_TRAIN
import at.museumrailwayevents.model.conventions.CommonKeys
import at.museumrailwayevents.model.conventions.RecurrenceType
import at.museumrailwayevents.model.conventions.Registration
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import java.time.OffsetDateTime

abstract class NoevogCollector(
    val jsoupCrawler: JsoupCrawler,
    operatorId: String,
    locationId: String,
    url: String,
    tags: List<String> = emptyList(),
    locationName: String
) : MuseumRailwayEventCollector(
    operatorId, locationId, url, tags, locationName
) {
    protected fun collectSonderfahrten(sonderfahrtenUrl: String, tags: String, locomotiveType: String): List<Event> {
        val events = mutableListOf<Event>()
        val document = jsoupCrawler.getDocument(sonderfahrtenUrl)

        val links = document.select("article.offer > h3 > a").eachAttr("href")
        links.forEach { eventUrl ->
            val eventDocument = jsoupCrawler.getDocument(eventUrl).select("section.content")
            val name = eventDocument.select("h1").eachText().first()
            val description = eventDocument.select("article.bodycopy > div > p").eachText().joinToString("\n")
            val dateStrings = eventDocument.select("h3:contains(Angebotszeitraum) ~ ul > li").eachText()

            if (dateStrings.size == 0) {
                println("no 'Angebotszeitraum' for $name found")
                return@forEach
            }

            val dates = dateStrings.flatMap { DateParser.parseAllDatesFrom(it) }

            val recurrence = when(dates.size) {
                1, 2 -> RecurrenceType.ONCE
                in 2..5 -> RecurrenceType.RARELY
                else -> RecurrenceType.REGULARLY
            }

            dates.forEach { date ->
                events.add(
                    createNoevogEvent(
                        name,
                        date,
                        eventUrl,
                        description,
                        recurrence,
                        locomotiveType,
                        tags
                    )
                )
            }
        }

        return events
    }

    private fun createNoevogEvent(
        name: String,
        date: OffsetDateTime,
        url: String,
        description: String,
        recurrenceType: String,
        locomotiveType: String,
        tags: String,
    ) = createEvent(
        name, date, additionalData = mutableMapOf(
            SemanticKeys.CATEGORY to CATEGORY_MUSEUM_TRAIN,
            SemanticKeys.URL to url,
            SemanticKeys.RECURRENCE_TYPE to recurrenceType,
            SemanticKeys.REGISTRATION to Registration.TICKET,
            SemanticKeys.DESCRIPTION to description,
            CommonKeys.LOCOMOTIVE_TYPE to locomotiveType,
            SemanticKeys.TAGS to tags,
        )
    )
}