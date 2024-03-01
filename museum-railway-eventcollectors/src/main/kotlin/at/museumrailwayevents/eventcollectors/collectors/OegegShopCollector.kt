package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.zoneOffset
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import base.boudicca.model.Event
import org.jsoup.Jsoup
import java.time.*

private val operatorId = "oegeg";
private val locationId_bahn = "oegeg_normalspur"
private val locationId_schiff = "oegeg_schifffahrt"
private val urlNormalspurTermine = "https://www.oegeg.at/termine/termine-normalspur-museum-lokpark-ampflwang/"
private val urlSchifffahrtsTermine = "https://www.oegeg.at/termine/termine-schifffahrt-dampfschiff-sch%C3%B6nbrunn/"

/**
 * Collects entries from ÖGEG shop pages. This includes Normalspur and Schifffahrt, but NOT Schmalspur.
 */
class OegegShopCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(operatorId, locationId_bahn, urlNormalspurTermine) {
    override fun collectEvents(): List<Event> {

        val events = mutableListOf<Event>()

        events.addAll(collectShopEvents(urlNormalspurTermine, locationId_bahn))
        events.addAll(collectShopEvents(urlSchifffahrtsTermine, locationId_schiff))
        return events
    }

    private fun collectShopEvents(url: String, locationId: String): MutableList<Event> {
        val document = jsoupCrawler.getDocument(url);
        val eventBoxes = document.select("div.cc-shop-product-desc")

        val events = mutableListOf<Event>()

        eventBoxes.forEach { eventBox ->
            val title = eventBox.select("h4").eachText().first()
            val description = eventBox.select("div.description > p").eachText().filter { it.length > 100 }.firstOrNull()

            val year = DateParser.findSingleYear(title)
            val month = DateParser.findSingleWrittenMonth(title)
            // TODO: this needs to be replaced to properly parse also date ranges
            val dates = DateParser.findAllDayDates(title)

            dates.forEach { date ->

                val startDate =
                    OffsetDateTime.of(
                        year,
                        month.value,
                        date.trim('.').toInt(),
                        0, 0, 0, 0,
                        zoneOffset)

                val additionalData = mutableMapOf<String, String>()
                if (!description.isNullOrBlank()) {
                    additionalData["description"] = description
                }

                events.add(createEvent(title, startDate, additionalData, locationId, url))
            }
        }

        return events
    }

    override fun getName(): String = "ÖGEG Normalspur Termine"

}