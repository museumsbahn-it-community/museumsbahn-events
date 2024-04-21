package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import org.jsoup.nodes.Element
import java.time.OffsetDateTime

class EbmSchwechatCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    "vef",
    "ebm_schwechat",
    "https://eisenbahnmuseum.at/veranstaltungen/termine/",
    locationName = "Eisenbahnmuseum Schwechat"
) {
    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(url)
        val events = mutableListOf<Event>()

        val boxes = document.select("div.su-box")

        boxes.forEach { box ->
            val title = box.select("div.su-box-title").text()
            val year = DateParser.findSingleYearOrNull(title)
            val currentYear = OffsetDateTime.now().year

            if (year == null || year != currentYear) {
                return@forEach
            }

            val eventList = box.select("ul")
            if (eventList.isNotEmpty()) {
                events.addAll(parseEventList(box))
            }

            val dates = DateParser.parseAllDatesFrom(title)
            dates.forEach { date ->
                val name = title.split(" am ").first() // currently assume alle events are like this
                val pictureUrl = box.select("img").attr("src")

                events.add(createEbmEvent(name, date, pictureUrl))
            }
        }

        return events
    }

    private fun parseEventList(eventListBox: Element): List<Event> {
        val textEntries = eventListBox.select("li").eachText()
        return textEntries.map { entry ->
            val split = entry.split("–").map { it.trim() }
            val date = DateParser.parseDate(split.first())
            val name = split[1]

            createEbmEvent(name, date)
        }
    }

    private fun createEbmEvent(name: String, date: OffsetDateTime, pictureUrl: String? = null): Event {
        val additionalData = mutableMapOf(
            SemanticKeys.DESCRIPTION to name,
            SemanticKeys.CATEGORY to if (name.hasTrain()) CATEGORY_MUSEUM_TRAIN else CATEGORY_RAILWAY_MUSEUM,
            SemanticKeys.TAGS to if (name.hasTrain()) TAGS_MUSEUM_RAILWAY_SPECIAL_TRIP.toTagsValue() else TAGS_MUSEUM_EVENT.toTagsValue(),
            SemanticKeys.REGISTRATION to Registration.TICKET,
        )
        if (pictureUrl != null) {
            additionalData[SemanticKeys.PICTURE_URL] = pictureUrl
        }
        return createEvent(
            name,
            date,
            additionalData
        )
    }

    private fun String.hasTrain() = this.lowercase().contains("pendelfahrt") || this.lowercase().contains("sonderzug")

    override fun getName(): String = "Eisenbahnmuseum Schwechat Collector"
}