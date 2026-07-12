package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.api.eventcollector.annotations.BoudiccaEventCollector
import base.boudicca.model.Event
import org.jsoup.nodes.Element
import java.time.OffsetDateTime

@BoudiccaEventCollector(collectorTypeName = "ebm_schwechat")
class EbmSchwechatCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    "vef",
    "ebm_schwechat",
    "https://eisenbahnmuseum.at/veranstaltungen/termine/",
    locationName = "Eisenbahnmuseum Schwechat"
) {
    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(sourceUrl)
        val events = mutableListOf<Event>()

        val boxes = document.select("div.su-box")

        boxes.forEach { box ->
            val title = box.select("div.su-box-title").text()
            DateParser.findSingleYearOrNull(title) ?: return@forEach

            val eventList = box.select("ul")
            if (eventList.isNotEmpty()) {
                events.addAll(parseEventList(box))
                return@forEach
            }

            try {
                val dates = DateParser.parseAllDatesFrom(title)
                dates.forEach { date ->
                    val name = title.split(" am ").first() // currently assume alle events are like this
                    val pictureUrl = box.select("img").attr("src")

                    events.add(createEbmEvent(name, date, pictureUrl))
                }
            } catch (ex: Exception) {
                println("could not parse box: $title")
            }
        }

        return events
    }

    private fun parseEventList(eventListBox: Element): List<Event> {
        val textEntries = eventListBox.select("li").eachText()
        return textEntries.map { entry ->
            try {
                val split = entry.split("–").map { it.trim() }
                val date = DateParser.parseDate(split.first())
                val name = split[1]

                createEbmEvent(name, date)
            } catch (ex: Exception) {
                println("exception parsing ebm schwechat event line: ${entry}, ex: ${ex.message}")
                null
            }
        }.filterNotNull()
    }

    private fun createEbmEvent(
        name: String,
        date: OffsetDateTime,
        pictureUrl: String? = null
    ): Event {
        val additionalData = mutableMapOf(
            SemanticKeys.DESCRIPTION to name,
            SemanticKeys.CATEGORY to if (name.hasTrain()) MuseumEventsCategory.SPECIAL_TRIP.jsonValue else MuseumEventsCategory.RAILWAY_MUSEUM.jsonValue,
            SemanticKeys.TAGS to if (name.hasTrain()) TAGS_MUSEUM_RAILWAY_SPECIAL_TRIP.toTagsValue() else TAGS_MUSEUM_EVENT.toTagsValue(),
            SemanticKeys.REGISTRATION to MuseumEventRegistration.TICKET.jsonValue,
        )
        if (pictureUrl != null) {
            additionalData[SemanticKeys.PICTURE_URL] = pictureUrl
        }
        return createEvent(
            name,
            date,
            sourceUrl,
            additionalData
        )
    }

    private fun String.hasTrain() = this.lowercase().contains("pendelfahrt") || this.lowercase().contains("sonderzug")

    override fun defaultDisplayName(): String = "Eisenbahnmuseum Schwechat Collector"
}