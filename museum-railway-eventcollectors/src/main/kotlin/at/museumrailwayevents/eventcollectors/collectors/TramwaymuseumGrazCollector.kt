package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import java.net.URI

class TramwaymuseumGrazCollector(private val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "tramwaymuseumgraz",
    locationId = "tramwaymuseumgraz",
    locationName = "Tramway Museum Graz",
    url = "https://www.tramway-museum-graz.at/kalender/"
) {
    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(url)
        val events = mutableListOf<Event>()

        val eventArticles = document.select("article.event")

        eventArticles.forEach { eventArticle ->
            val name = eventArticle.select("h3").text()
            val dateString = eventArticle.select("div.row:contains(schedule) div").text()
            val dates = DateParser.parseAllDatesFrom(dateString)
            val locationString = eventArticle.select("div.row:contains(location_on) div").text()
            val paymentsString = eventArticle.select("div.row:contains(location_on) div").text()

            val detailsUrlRaw = eventArticle.select("a:contains(Details)").attr("href")
            val detailsUrl = if (detailsUrlRaw.startsWith("http")) {
                detailsUrlRaw
            } else {
                URI(url + detailsUrlRaw).toString()
            }
            // TODO add caching
            val detailsPage = jsoupCrawler.getDocument(detailsUrl)
            val descriptionBlock = detailsPage.select("div.row:contains(Beschreibung)")
            val description = descriptionBlock.select("p,ul,ol").eachText().joinToString("\n")
            val descriptionWithPayment = "$description\n$paymentsString"
            val imageUrl = URI(url + eventArticle.select("img").attr("src")).toString()

            dates.forEach { date ->
                events.add(
                    createEvent(
                        name,
                        date,
                        mutableMapOf(
                            SemanticKeys.CATEGORY to CATEGORY_RAILWAY_MUSEUM, // TODO improve category
                            SemanticKeys.REGISTRATION to Registration.PRE_SALES_ONLY, // sometimes also tickets on location
                            SemanticKeys.DESCRIPTION to descriptionWithPayment,
                            SemanticKeys.LOCATION_NAME to locationString,
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
