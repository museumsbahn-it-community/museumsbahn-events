package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import base.boudicca.model.Event
import java.time.*
import java.util.*

class WackelsteinexpressCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "wackelsteinexpress",
    locationId = "wackelsteinexpress",
    url = "https://www.wackelsteinexpress.at/",
) {
    val locale = Locale.GERMAN
    val offset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())
    val regularEventsUrl = "https://www.wackelsteinexpress.at/fahrplan/"
    val themenfahrtenUrl = "https://reservierung.wackelsteinexpress.at/"


    data class DepartureData(val startTime: OffsetDateTime, val station: String, val traction: String)

    override fun collectEvents(): List<Event> {


        val events = mutableListOf<Event>()

        return events
    }

    private fun collectThemenfahrten(url: String): List<Event> {
        val document = jsoupCrawler.getDocument(regularEventsUrl)
        val content = document.select("div.entryContent")
        val dateString = content.select("p").eachText().first { DateParser.isDurationString(it) }

        return emptyList()
    }

    private fun collectRegularEvents(url: String): List<Event> {

        return emptyList()
    }

    override fun getName(): String = "Wackelsteinexpress Collector"
}