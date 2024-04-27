package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.Tags
import base.boudicca.model.Event

class AtterseebahnCollector(jsoupCrawler: JsoupCrawler) : SternHafferlCollector(
    jsoupCrawler,
    Tags.LOCOMOTIVE_TYPE_TRAM,
    operatorId = "sth",
    locationId = "atterseebahn",
    locationName = "Atterseebahn Nostalgie",
    url = "https://www.stern-verkehr.at/ausfluege-events/nostalgiefahrt-attersee/"
) {
    override fun collectEvents(): List<Event> = collectEventsFromPage(url)

    override fun getName(): String = "Atterseebahn Collector"
}