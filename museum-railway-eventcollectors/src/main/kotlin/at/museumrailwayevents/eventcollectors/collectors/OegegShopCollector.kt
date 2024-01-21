package at.museumsbahnevents.eventcollectors.collectors

import base.boudicca.model.Event
import org.jsoup.Jsoup
import java.time.*
import java.util.*

private val operatorId = "oegeg";
private val locationId_bahn = "oegeg_normalspur"
private val locationId_schiff = "oegeg_schifffahrt"
private val urlNormalspurTermine = "https://www.oegeg.at/termine/termine-normalspur-museum-lokpark-ampflwang/"
private val urlSchifffahrtsTermine = "https://www.oegeg.at/termine/termine-schifffahrt-dampfschiff-sch%C3%B6nbrunn/"

/**
 * Collects entries from ÖGEG shop pages. This includes Normalspur and Schifffahrt, but NOT Schmalspur.
 */
class OegegShopCollector : MuseumRailwayEventCollector(operatorId, locationId_bahn, urlNormalspurTermine) {
    override fun collectEvents(): List<Event> {

        val events = mutableListOf<Event>()

        events.addAll(collectShopEvents(urlNormalspurTermine, locationId_bahn))
        events.addAll(collectShopEvents(urlSchifffahrtsTermine, locationId_schiff))
        return events
    }

    private fun collectShopEvents(url: String, locationId: String): MutableList<Event> {
        val document = Jsoup.connect(url).get()
        val eventBoxes = document.select("div.cc-shop-product-desc")

        val locale = Locale.GERMAN
        val events = mutableListOf<Event>()

        eventBoxes.forEach { eventBox ->
            val title = eventBox.select("h4").eachText().first()
            val description = eventBox.select("div.description > p").eachText().filter { it.length > 100 }.firstOrNull()

            // dates on this website can be split having multiple dates in the same line
            // eg. Sonntag, 4., 11., 18. und 25. Juni 2023
            val years = yearRegex.findAll(title)
            assert(years.count() == 1)
            val year = years.first().value.trim().toInt()

            val months = monthRegex.findAll(title.lowercase())
            assert(months.count() == 1)
            val monthName = months.first().value.trim()

            val dates = dateRegex.findAll(title)

            val offset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())
            dates.forEach { date ->
                val month = convertMonthNameToMonth(monthName, locale)
                val startDate =
                    OffsetDateTime.of(
                        year,
                        month.value,
                        date.value.trim('.').toInt(),
                        0, 0, 0, 0,
                        offset)

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