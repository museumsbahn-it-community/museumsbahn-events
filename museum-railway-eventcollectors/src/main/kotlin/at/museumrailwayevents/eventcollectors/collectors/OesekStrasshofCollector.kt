package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.keepLineBreaks
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import java.time.LocalDate
import java.time.OffsetDateTime

class OesekStrasshofCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    "oesek",
    "oesek",
    "https://eisenbahnmuseum-heizhaus.com/de/veranstaltungen",
    locationName = "Eisenbahnmuseum Strasshof"
) {
    override fun collectEvents(): List<Event> {
        val baseUrl = "https://eisenbahnmuseum-heizhaus.com/"
        val document = jsoupCrawler.getDocument(sourceUrl)
        val eventLinks = document.select("div.content h2 a")
        val pictureUrls = document.select("div.content figure.image_container img").map { baseUrl + it.attr("src") }

        // this currently only collects museum events, but there are no special trips announced
        // so at the moment we do not have the structure to parse

        return eventLinks.mapIndexed { index, eventLink ->
            val relativeUrl = eventLink.attr("href")
            val eventUrl = baseUrl + relativeUrl
            val eventDocument = jsoupCrawler.getDocument(eventUrl)

            val time = eventDocument.select("p.info time").attr("datetime")
            val startDate = try {
                // TODO: we have to handle that this has the correct offset :D
                OffsetDateTime.parse(time)
            } catch (ex: Exception) {
                LocalDate.parse(time).atTime(0, 0).atOffset(DateParser.zoneOffset)
            }
            val titleElement = eventDocument.select("h1")
            val title = titleElement.first()?.text() ?: "Eisenbahnmuseum Strasshof"
            val description = eventDocument.keepLineBreaks().select("div.block").text()
            val dampftagIcon = titleElement.first()?.select("img")
            val isDampftag = !dampftagIcon.isNullOrEmpty() && dampftagIcon.attr("src").contains("icon-dampflok.svg")
            val pictureUrl = pictureUrls[index]

            val event = createStructuredEvent(
                title,
                startDate,
                eventUrl,
                mutableMapOf(
                    SemanticKeys.DESCRIPTION_TEXT_PROPERTY.getKey(language = "de") to description,
                    SemanticKeys.CATEGORY_PROPERTY.getKey() to MuseumEventsCategory.MUSEUM_EVENT.jsonValue,
                    SemanticKeys.PICTURE_URL_PROPERTY.getKey() to pictureUrl,
                    SemanticKeys.REGISTRATION_PROPERTY.getKey() to MuseumEventRegistration.TICKET.jsonValue,
                    SemanticKeys.RECURRENCE_TYPE_PROPERTY.getKey() to RecurrenceType.RARELY,
                )
            ).toBuilder()

            if (isDampftag) {
                event.withProperty(CommonKeys.VEHICLE_TYPE_PROPERTY, VehicleType.STEAM_TRAIN)
            }

            event.build().toFlatEvent()
        }
    }


    override fun getName(): String = "Eisenbahnmuseum Strasshof Collector"
}