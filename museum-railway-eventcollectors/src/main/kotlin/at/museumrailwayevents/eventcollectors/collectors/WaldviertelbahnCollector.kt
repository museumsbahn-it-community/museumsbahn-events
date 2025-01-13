package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.TAGS_NARROW_GAUGE
import at.museumrailwayevents.model.conventions.Tags
import at.museumrailwayevents.model.conventions.VehicleType
import base.boudicca.model.Event

/**
 * There are offers for golden DMUs, but these are excluded here, because
 * these reflect more of a regular train service than historic trains.
 */
class WaldviertelbahnCollector(jsoupCrawler: JsoupCrawler) : NoevogCollector(
    jsoupCrawler,
    operatorId = "noevog",
    locationId = "waldviertelbahn",
    locationName = "Waldviertelbahn",
    url = "https://www.waldviertelbahn.at/"
) {

    val eventOverviewPageUrls = listOf(
        Pair("https://www.waldviertelbahn.at/nostalgie-diesel-1", VehicleType.DIESEL_TRAIN),
        Pair("https://www.waldviertelbahn.at/nostalgie-dampf", VehicleType.STEAM_TRAIN)
    )


    override fun collectEvents(): List<Event> {
        return eventOverviewPageUrls.flatMap { eventOverviewPage ->
            collectSonderfahrten(eventOverviewPage.first, TAGS_NARROW_GAUGE.toTagsValue(), eventOverviewPage.second)
        }

    }

    override fun getName(): String = "Waldviertelbahn Collector"
}