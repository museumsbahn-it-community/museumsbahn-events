package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.VehicleType
import base.boudicca.model.Event

class HoellentalbahnCollector(jsoupCrawler: JsoupCrawler) : OeglbCollector(
    jsoupCrawler,
    VehicleType.ELECTRIC_TRAIN,
    operatorId = "oeglb",
    locationId = "hoellentalbahn",
    locationName = "Höllentalbahn",
    sourceUrl = "https://www.lokalbahnen.at/hoellentalbahn/"
) {

    private val baseUrl = "https://www.lokalbahnen.at/"

    private val fahrplanUrls = listOf(
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/fahrplan/fahrplan-sommer/",
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/fahrplan/fahrplan-herbst/"
    )

    private val sonderfahrtenUrl = "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/sonderveranstaltungen/"

    override fun collectEvents(): List<Event> {
        val regularEvents = fahrplanUrls.flatMap { collectFahrplanPage(it) }
        val sonderfahrten = collectSonderfahrten(baseUrl, sonderfahrtenUrl)
        return regularEvents + sonderfahrten
    }

    override fun getName(): String = "Höllentalbahn Collector"

}
