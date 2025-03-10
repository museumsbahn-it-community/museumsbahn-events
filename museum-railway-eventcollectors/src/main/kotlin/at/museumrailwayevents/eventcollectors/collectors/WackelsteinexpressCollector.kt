package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.parseAllTimesFrom
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import org.jsoup.nodes.Document
import java.util.*

class WackelsteinexpressCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "wackelsteinexpress",
    locationId = "wackelsteinexpress",
    locationName = "Wackelsteinexpress",
    sourceUrl = "https://www.wackelsteinexpress.at/",
) {
    val locale = Locale.GERMAN
    val regularEventsUrl = "https://www.wackelsteinexpress.at/fahrplan/"
    val themenfahrtenUrl = "https://reservierung.wackelsteinexpress.at/"
    val vehicleType = VehicleType.DIESEL_TRAIN // wackelsteinexpress faehrt nur Diesel

    override fun collectEvents(): List<Event> {
        val events = collectThemenfahrten(themenfahrtenUrl)
        // TODO: also collect regular events
        return events
    }

    private fun collectThemenfahrten(url: String): List<Event> {
        var nextPageUrl: String? = url
        val themenfahrten = mutableListOf<Event>()

        while (nextPageUrl != null) {
            val document = jsoupCrawler.getDocument(nextPageUrl)
            themenfahrten.addAll(collectThemenfahrtenPage(document))

            nextPageUrl = document.selectFirst("a.next")?.attr("href")
        }

        return themenfahrten
    }

    private fun collectThemenfahrtenPage(document: Document): List<Event> {
        val events = mutableListOf<Event>()
        val content = document.select("li > a").toList().filter { it.select("h2").size > 0 }
        content.forEach { entry ->
            val eventUrl = entry.attr("href")
            val detailsPage = jsoupCrawler.getDocument(eventUrl)
            println(eventUrl)
            val textContent = detailsPage.selectFirst("h1")?.html()?.split("<br>")?.map { it.trim() }
            val imageUrl = detailsPage.selectFirst("img.wp-post-image")?.attr("src")
            val description = detailsPage.selectFirst("#tab-description")?.text()

            if (textContent == null) {
                return@forEach
            }

            val name = textContent[0]
            // TODO: Sommerfahrten (jeden Mittwoch) are causing trouble here. need to fix.
            val date = DateParser.tryParseDate(textContent[1]) ?: return@forEach
            val time = parseAllTimesFrom(textContent[2]).firstOrNull()

            val dateTime = if (time == null) {
                date
            } else {
                time.atDate(date.toLocalDate())
            }

            val additionalData = mutableMapOf(
                CommonKeys.VEHICLE_TYPE to vehicleType,
                SemanticKeys.REGISTRATION to MuseumEventRegistration.PRE_SALES_ONLY.jsonValue,
                SemanticKeys.RECURRENCE_TYPE to RecurrenceType.RARELY,
                SemanticKeys.CATEGORY to MuseumEventsCategory.SPECIAL_TRIP.jsonValue,
                SemanticKeys.TAGS to Tags.NARROW_GAUGE,
            )

            if (description != null) {
                additionalData[SemanticKeys.DESCRIPTION] = description
            }
            if (imageUrl != null) {
                additionalData[SemanticKeys.PICTURE_URL] = imageUrl
            }

            events.add(
                createEvent(
                    name,
                    dateTime,
                    eventUrl,
                    additionalData
                )
            )
        }
        return events
    }

    private fun collectRegularEvents(url: String): List<Event> {
        val document = jsoupCrawler.getDocument(url)
        val content = document.select("div.entryContent")
        val dateString = content.select("p").eachText().first { DateParser.isDurationString(it) }

        // TODO: regular events require parsing of the repeating string, so for now we only collect the special trips
        return emptyList()
    }

    override fun getName(): String = "Wackelsteinexpress Collector"
}