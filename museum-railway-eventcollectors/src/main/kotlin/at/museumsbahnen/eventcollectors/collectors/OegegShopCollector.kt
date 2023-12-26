package at.museumsbahnen.eventcollectors.collectors

import base.boudicca.api.eventcollector.EventCollector
import base.boudicca.model.Event
import org.jsoup.Jsoup
import java.time.*
import java.util.*

/**
 * Collects entries from ÖGEG shop pages. This includes Normalspur and Schifffahrt, but NOT Schmalspur.
 */
class OegegShopCollector : EventCollector {
    override fun collectEvents(): List<Event> {
        val normalspurTermine = "https://www.oegeg.at/termine/termine-normalspur-museum-lokpark-ampflwang/"
        val schifffahrtsTermine = "https://www.oegeg.at/termine/termine-schifffahrt-dampfschiff-sch%C3%B6nbrunn/"

        val events = mutableListOf<Event>()

        events.addAll(collectShopEvents(normalspurTermine, type="Sonderfahrt Nostalgiezug"))
        events.addAll(collectShopEvents(schifffahrtsTermine, type="Sonderfahrt Nostalgieschiff"))
        return events
    }

    private fun collectShopEvents(url: String, type: String): MutableList<Event> {
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
                additionalData["type"] = type
                if (!description.isNullOrBlank()) {
                    additionalData["description"] = description
                }

                events.add(Event(title, startDate, additionalData))
            }
        }

        return events
    }

    override fun getName(): String = "ÖGEG Normalspur Termine"

}