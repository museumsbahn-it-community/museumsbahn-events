package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.VehicleType
import base.boudicca.model.Event

class AtterseebahnCollector(jsoupCrawler: JsoupCrawler) : SternHafferlCollector(
    jsoupCrawler,
    VehicleType.ELECTRIC_TRAIN,
    operatorId = "sth",
    locationId = "atterseebahn",
    locationName = "Atterseebahn Nostalgie",
    sourceUrl = "https://www.stern-verkehr.at/ausfluege-events/nostalgiefahrt-attersee/",
    eventJsonApiUrl = "https://www.stern-verkehr.at/wp-json/ausfluege-events/v1/events",
    filterExcludeStrings = listOf("traunsee", "traunseetram", "gmunden")
) {
    override fun collectEvents(): List<Event> = collectEventsFromPage(sourceUrl)

    override fun getName(): String = "Atterseebahn Collector"
}