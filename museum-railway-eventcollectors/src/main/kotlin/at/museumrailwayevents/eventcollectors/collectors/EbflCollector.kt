package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event

const val locationId_ebfl_museum = "ebfl_museum"
const val locationId_ebfl_suedbahn_express = "ebfl_suedbahn_express"

/**
 * Currently we can only collect the excursion trips of Südbahn Express, as the
 * events on the museum page are too unstructured to be parsed automatically
 * (special events and regular opening hours are mixed).
 */
class EbflCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "ebfl",
    locationId = locationId_ebfl_museum,
    locationName = "EBFL",
    sourceUrl = "https://ebfl.at/"
) {
    private val museumUrl = "https://ebfl.at/index.php/suedbahn-heizhaus/"
    private val suedbahnExpressUrl = "https://ebfl.at/index.php/termine-ausfahrten/"

    private val excludedLinkTexts = listOf(
        "Nostalgiezug Südbahn Express",
        "Teilnahmebedingungen"
    )

    override fun collectEvents(): List<Event> {
        return collectMuseumEvents() + collectSuedbahnExpressEvents()
    }

    private fun collectMuseumEvents(): List<Event> {
        // the page is simply not structured enough to extract meaningful data :/
        return emptyList()
    }

    private fun collectSuedbahnExpressEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(suedbahnExpressUrl)
        val content = document.select("div#content")
        val eventUrls = content.select("a:contains(Mehr dazu erfahren Sie hier)").map { it.attr("href") }
        val events = mutableListOf<Event>()

        eventUrls.forEach { eventUrl ->

            if (!eventUrl.startsWith(this.sourceUrl)) {
                // only crawl sites on the same page
                return@forEach
            }

            val eventPage = jsoupCrawler.getDocument(eventUrl)
            val name = eventPage.select("h1.title").text()
            val eventContent = eventPage.select("div#content")
            val paragraphs = eventContent.select("p").eachText()
            if (paragraphs.size == 0) {
                println("error: empty paragraphs for eventUrl: $eventUrl, name: $name")
                return@forEach
            }

            val eventPictureUrl = eventContent.select("img").first()?.attr("src")

            val dateString = paragraphs.first()
            val description = paragraphs.drop(1).joinToString("\n")

            try {
                val dates = DateParser.parseAllDatesFrom(dateString)
                //val eventPictureUrl = eventUrls.second
                val additionalData = mutableMapOf(
                    SemanticKeys.CATEGORY to MuseumEventsCategory.SPECIAL_TRIP.jsonValue,
                    SemanticKeys.URL to eventUrl,
                    SemanticKeys.RECURRENCE_TYPE to RecurrenceType.ONCE,
                    SemanticKeys.REGISTRATION to MuseumEventRegistration.PRE_SALES_ONLY.jsonValue,
                    SemanticKeys.DESCRIPTION to description,
                    CommonKeys.VEHICLE_TYPE to VehicleType.ELECTRIC_TRAIN, // EBFL is only running electric trains, right?
                    SemanticKeys.TAGS to TAGS_MUSEUM_RAILWAY_SPECIAL_TRIP.toTagsValue(),
                    SemanticKeys.ADDITIONAL_EVENTS_URL to suedbahnExpressUrl,
                )

                if (eventPictureUrl != null) {
                    additionalData[SemanticKeys.PICTURE_URL] = eventPictureUrl
                }

                dates.forEach { date ->
                    events.add(
                        createEvent(
                            name,
                            date,
                            eventUrl,
                            additionalData,
                            locationId_ebfl_suedbahn_express,
                            eventUrl
                        )
                    )
                }
            } catch (ex: Exception) {
                // sometimes they publish a "Reisebericht" on their page where we cannot parse dates
                println("[EBFL Collector] could not parse event from url: $eventUrl")
                println("[EBFL Collector] exception: ${ex.message}")
                println(ex.stackTrace)
            }
        }

        return events
    }

    override fun getName(): String = "EBFL Collector"


}