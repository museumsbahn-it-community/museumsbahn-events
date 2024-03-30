package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.Tags
import base.boudicca.model.Event

class YbbstalbahnCollector(jsoupCrawler: JsoupCrawler) : OeglbCollector(
    jsoupCrawler,
    Tags.LOCOMOTIVE_TYPE_DIESEL,
    operatorId = "oeglb",
    locationId = "ybbstalbahn",
    locationName = "Ybbstalbahn Bergstrecke",
    url = "https://www.lokalbahnen.at/bergstrecke/"
) {

    private val fahrplanUrls = listOf(
        "https://www.lokalbahnen.at/bergstrecke/mitfahren/fahrplan/"
    )

    private val sonderfahrtenUrl = "https://www.lokalbahnen.at/bergstrecke/mitfahren/sonderveranstaltungen/"

    override fun collectEvents(): List<Event> {
        val regularEvents = fahrplanUrls.flatMap { collectFahrplanPage(it) }
        val sonderfahrten = collectSonderfahrten(sonderfahrtenUrl)
        return regularEvents + sonderfahrten
    }

    override fun getName(): String = "Ybbstalbahn Collector"

}