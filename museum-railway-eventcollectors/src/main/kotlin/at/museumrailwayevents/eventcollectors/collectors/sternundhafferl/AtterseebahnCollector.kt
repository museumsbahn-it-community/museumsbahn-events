package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.Tags
import base.boudicca.model.Event

class AtterseebahnCollector(jsoupCrawler: JsoupCrawler) : SternHafferlCollector(
    jsoupCrawler,
    Tags.LOCOMOTIVE_TYPE_TRAM,
    operatorId = "sth",
    locationId = "atterseebahn",
    locationName = "Atterseebahn Nostalgie",
    url = "https://www.stern-verkehr.at/ausfluege-events/nostalgiefahrt-attersee/",
    eventJsonApiUrl = "https://www.stern-verkehr.at/wp-json/ausfluege-events/v1/events",
    filterExcludeStrings = listOf("traunsee", "traunseetram", "gmunden")
) {
    override fun collectEvents(): List<Event> = collectEventsFromPage(url)

    override fun getName(): String = "Atterseebahn Collector"
}