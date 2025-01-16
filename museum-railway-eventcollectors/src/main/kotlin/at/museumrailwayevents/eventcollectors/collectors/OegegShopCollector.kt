package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event

private val operatorId = "oegeg";
private val locationId_bahn = "oegeg_normalspur"
private val locationId_schiff = "oegeg_schifffahrt"
private val urlNormalspurTermine = "https://www.oegeg.at/termine/termine-normalspur-museum-lokpark-ampflwang/"
private val urlSchifffahrtsTermine = "https://www.oegeg.at/termine/termine-schifffahrt-dampfschiff-sch%C3%B6nbrunn/"

/**
 * Collects entries from ÖGEG shop pages. This includes Normalspur and Schifffahrt, but NOT Schmalspur.
 */
class OegegShopCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId, locationId_bahn, urlNormalspurTermine, locationName = "ÖGEG"
) {
    override fun collectEvents(): List<Event> {

        val events = mutableListOf<Event>()

        events.addAll(collectShopEvents(urlNormalspurTermine, locationId_bahn, null))
        events.addAll(collectShopEvents(urlSchifffahrtsTermine, locationId_schiff, VehicleType.SHIP))
        return events
    }

    private fun collectShopEvents(url: String, locationId: String, vehicleType: String?): MutableList<Event> {
        val document = jsoupCrawler.getDocument(url);
        val eventBoxes = document.select("div.hproduct")

        val events = mutableListOf<Event>()

        eventBoxes.forEach { eventBox ->
            val title = eventBox.select("h4").eachText().first()
            val description = eventBox.select("div.description > p").eachText().filter { it.length > 100 }.firstOrNull()

            val dates = DateParser.parseAllDatesFrom(title)
            val pictureUrl = eventBox.select("div.cc-shop-product-img").select("a").attr("href")

            dates.forEach { date ->
                val additionalData = mutableMapOf<String, String>()
                if (!description.isNullOrBlank()) {
                    additionalData[SemanticKeys.DESCRIPTION] = description
                }

                if (!pictureUrl.isNullOrBlank()) {
                    additionalData[SemanticKeys.PICTURE_URL] = pictureUrl
                }

                if (vehicleType != null) {
                    additionalData[CommonKeys.VEHICLE_TYPE] = vehicleType
                }
                additionalData[SemanticKeys.REGISTRATION] = Registration.PRE_SALES_ONLY
                additionalData[SemanticKeys.CATEGORY] = Category.SPECIAL_TRIP
                additionalData[SemanticKeys.RECURRENCE_TYPE] = RecurrenceType.ONCE

                events.add(createEvent(title, date, url, additionalData, locationId, url))
            }
        }

        return events
    }

    override fun getName(): String = "ÖGEG Normalspur Termine"

}