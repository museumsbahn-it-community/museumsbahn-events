package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import java.net.URI
import java.time.OffsetDateTime

abstract class OeglbCollector(
    val jsoupCrawler: JsoupCrawler,
    val locomotiveType: String,
    operatorId: String,
    locationId: String,
    sourceUrl: String,
    tags: List<String> = emptyList(),
    locationName: String
) : MuseumRailwayEventCollector(
    operatorId, locationId, sourceUrl, tags, locationName
) {
    protected fun collectSonderfahrten(baseUrl: String, sonderfahrtenUrl: String): List<Event> {
        val events = mutableListOf<Event>()

        val document = jsoupCrawler.getDocument(sonderfahrtenUrl)
        val emptyMessage = document.select("p:contains(Derzeit sind keine Sonderveranstaltungen geplant.)")

        if (emptyMessage.size > 0) {
            return events
        }

        val links = document.select("a.card-link").eachAttr("href")
        links.forEach { eventUrl ->
            val fullEventUrl = URI("$baseUrl$eventUrl").normalize().toString()
            val eventDocument = jsoupCrawler.getDocument(fullEventUrl)
            val name = eventDocument.select("h1").eachText().first()
            val description = eventDocument.select("h1 ~ p").eachText().filterNot {
                it.contains("Details zu dieser Veranstaltung") ||
                        it.contains("Zurück")
            }.joinToString("\n")
            val date = DateParser.parseDate(description)

            events.add(createOeglbEvent(
                name,
                date,
                fullEventUrl,
                description,
                RecurrenceType.ONCE
            ))
        }

        return events
    }

    protected fun collectFahrplanPage(url: String): List<Event> {
        val events = mutableListOf<Event>()
        val document = jsoupCrawler.getDocument(url)

        val content = document.select(".container-xl")
        val validity = content.select("p").eachText().first()
        val informationHeading = content.select("h3:contains(Information)").eachText().first()
        val information = content.select("h3:contains(Information) ~ p").eachText()

        val description = listOf(validity, informationHeading, information.joinToString("\n")).joinToString("\n")
        val calendars = content.select("table.kalender")

        calendars.forEach { calendar ->
            val monthString = calendar.select("td.monat").eachText().first()
            val year = DateParser.findSingleYear(monthString)
            val month = DateParser.findSingleMonth(monthString)
            val days = calendar.select("td.tag").eachText().map { DateParser.findAllDays(it).first() }

            days.forEach { day ->
                val date = DateParser.createDate(year, month, day)
                events.add(
                    createOeglbEvent(
                        locationName,
                        date,
                        url,
                        description,
                        RecurrenceType.REGULARLY
                    )
                )
            }
        }

        return events
    }

    private fun createOeglbEvent(
        name: String,
        date: OffsetDateTime,
        eventUrl: String,
        description: String,
        recurrenceType: String,
    ) = createEvent(
        name, date, eventUrl, additionalData = mutableMapOf(
            SemanticKeys.CATEGORY to Category.MUSEUM_RAILWAY,
            SemanticKeys.RECURRENCE_TYPE to recurrenceType,
            SemanticKeys.REGISTRATION to Registration.TICKET,
            SemanticKeys.DESCRIPTION to description,
            CommonKeys.VEHICLE_TYPE to locomotiveType,
            SemanticKeys.TAGS to TAGS_NARROW_GAUGE.toTagsValue(),
        )
    )
}