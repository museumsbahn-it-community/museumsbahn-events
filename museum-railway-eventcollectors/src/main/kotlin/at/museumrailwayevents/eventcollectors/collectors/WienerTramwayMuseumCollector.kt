package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import java.net.URI

class WienerTramwayMuseumCollector(private val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "wtm",
    locationId = "wtm",
    locationName = "Wiener Tramway Museum",
    url = "https://tram.at/"
) {
    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(url)
        val events = mutableListOf<Event>()

        val newsBlock = document.select("div:has(> div > div > h2:contains(Aktuelles)) + div")
        val eventArticles = newsBlock.select("article")

        eventArticles.forEach { eventArticleEntry ->
            val detailsUrl = eventArticleEntry.select("a").attr("href")
            val detailsPage = jsoupCrawler.getDocument(detailsUrl)
            val name = detailsPage.select("h2").text()
            val dateString1 = detailsPage.select("h1").text()
            val dateString2 = detailsPage.select("h2+h3").text()
            // wtm is kinda annoying, because sometimes they design their pages differentlys
            val dateString = dateString1.ifBlank {
                dateString2
            }
            val dates = DateParser.parseAllDatesFrom(dateString)

            val description = detailsPage.select("h2 ~ p,ul,h3").eachText().joinToString("\n")
            val imageUrl = URI(url + eventArticleEntry.select("img").attr("src")).toString()

            dates.forEach { date ->
                events.add(
                    createEvent(
                        name,
                        date,
                        mutableMapOf(
                            SemanticKeys.CATEGORY to CATEGORY_MUSEUM_TRAIN,
                            SemanticKeys.REGISTRATION to Registration.TICKET,
                            SemanticKeys.DESCRIPTION to description,
                            CommonKeys.LOCOMOTIVE_TYPE to Tags.LOCOMOTIVE_TYPE_TRAM,
                            SemanticKeys.RECURRENCE_TYPE to RecurrenceType.RARELY,
                            SemanticKeys.TAGS to TAGS_MUSEUM_EVENT.toTagsValue(),
                            SemanticKeys.PICTURE_URL to imageUrl,
                        ),
                    )
                )
            }
        }

        return events
    }

    override fun getName(): String = "Tramwaymuseum Graz Collector"

}
