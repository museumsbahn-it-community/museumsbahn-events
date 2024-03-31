package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event

/**
 * This collector only collects the Mh6 Events. Mariazellerbahn steam train and Ötscherbär are covered separately.
 */
class Mh6Collector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "mh6",
    locationId = "mh6_heizhaus_krumpe",
    locationName = "Verein Mh.6 Heizhaus Ober-Grafendorf und Krumpe",
    url = "http://www.mh6.at/"
) {
    private val eventsUrl = "http://www.mh6.at/de/termine/"

    data class EventEntry(val dateString: String, val title: String, val eventUrl: String)

    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(eventsUrl)
        val rows = document.select("tr")
        val entries = rows.map { row ->
            val cols = row.select("td")
            if (cols.size < 3) {
                return@map null
            }

            EventEntry(cols[0].text(), cols[1].text(), cols[2].select("a").attr("href"))
        }.filterNotNull().filterNot {
            // filter out steam trains as these are handled in another collector
            it.title.contains("nach Mariazell")
        }

        val events = mutableListOf<Event>()

        entries.forEach { eventEntry ->
            val eventUrl = eventEntry.eventUrl
            val eventPage = jsoupCrawler.getDocument(eventUrl)
            val pageTitle = eventPage.select("h1.entry-title").text()
            val paragraphs = eventPage.select("div.entry-content > p").eachText()
            val description = "$pageTitle\n\n${paragraphs.joinToString("\n")}"
            val imageUrl = eventPage.select("img").attr("src")

            val date = DateParser.parseDate(eventEntry.dateString)
            val additionalData = mutableMapOf(
                SemanticKeys.CATEGORY to CATEGORY_MUSEUM_TRAIN,
                SemanticKeys.URL to eventUrl,
                SemanticKeys.RECURRENCE_TYPE to RecurrenceType.RARELY,
                SemanticKeys.REGISTRATION to Registration.TICKET,
                SemanticKeys.DESCRIPTION to description,
                CommonKeys.LOCOMOTIVE_TYPE to Tags.LOCOMOTIVE_TYPE_DIESEL, // EBFL is only running electric trains, right?
                SemanticKeys.TAGS to TAGS_NARROW_GAUGE.toTagsValue(),
                SemanticKeys.ADDITIONAL_EVENTS_URL to eventsUrl,
            )

            if (imageUrl.isNotBlank()) {
                additionalData[SemanticKeys.PICTURE_URL] = imageUrl
            }

            events.add(
                createEvent(
                    eventEntry.title,
                    date,
                    additionalData,
                    locationId_ebfl_suedbahn_express,
                    eventUrl
                )
            )
        }

        return events
    }

    override fun getName(): String = "Mh6 Collector"


}