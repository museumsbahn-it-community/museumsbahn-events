package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.TAGS_MUSEUM_RAILWAY_OPERATING
import at.museumrailwayevents.model.conventions.Tags
import at.museumrailwayevents.model.conventions.VehicleType
import base.boudicca.model.Event

/**
 * There are offers for golden DMUs, but these are excluded here, because
 * these reflect more of a regular train service than historic trains.
 */
class ReblausexpressCollector(jsoupCrawler: JsoupCrawler) : NoevogCollector(
    jsoupCrawler,
    operatorId = "noevog",
    locationId = "reblausexpress",
    locationName = "Reblausexpress",
    url = "https://www.reblausexpress.at/"
) {

    val eventOverviewPageUrls = listOf(
        Pair("https://www.reblausexpress.at/tickets-angebote-reb", VehicleType.DIESEL_TRAIN)
    )

    override fun collectEvents(): List<Event> {
        return eventOverviewPageUrls.flatMap { eventOverviewPage ->
            collectSonderfahrten(
                eventOverviewPage.first,
                TAGS_MUSEUM_RAILWAY_OPERATING.toTagsValue(),
                eventOverviewPage.second,
                // we have to exclude these events otherwise they will spam the complete event list
                excludeByTitle = listOf(Regex("Saisonkarte"),
                    Regex("Retzer Erlebniskeller"),
                    Regex("Stadtführung Drosendorf")
                    ),
                excludeByBody = listOf(Regex("Zielbahnhof")),
                removeFromTitle = listOf(Regex("Ticket"))
            )
        }

    }

    override fun getName(): String = "Reblausexpress Collector"
}