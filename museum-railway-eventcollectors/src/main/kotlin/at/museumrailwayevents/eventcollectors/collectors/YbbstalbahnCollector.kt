package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.VehicleType
import base.boudicca.model.Event

class YbbstalbahnCollector(jsoupCrawler: JsoupCrawler) : OeglbCollector(
    jsoupCrawler,
    VehicleType.DIESEL_TRAIN, // jan 2025: i hope they will ever run the steamer again :D
    operatorId = "oeglb",
    locationId = "ybbstalbahn",
    locationName = "Ybbstalbahn Bergstrecke",
    sourceUrl = "https://www.lokalbahnen.at/bergstrecke/"
) {

    private val baseUrl = "https://www.lokalbahnen.at/"

    private val fahrplanUrls = listOf(
        "https://www.lokalbahnen.at/bergstrecke/mitfahren/fahrplan/"
    )

    private val sonderfahrtenUrl = "https://www.lokalbahnen.at/bergstrecke/mitfahren/sonderveranstaltungen/"

    override fun collectEvents(): List<Event> {
        val regularEvents = fahrplanUrls.flatMap { collectFahrplanPage(it) }
        val sonderfahrten = collectSonderfahrten(baseUrl, sonderfahrtenUrl)
        return regularEvents + sonderfahrten
    }

    override fun getName(): String = "Ybbstalbahn Collector"

}