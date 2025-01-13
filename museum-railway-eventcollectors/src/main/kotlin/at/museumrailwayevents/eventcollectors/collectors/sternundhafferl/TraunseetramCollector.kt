package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.VehicleType
import base.boudicca.model.Event

class TraunseetramCollector(jsoupCrawler: JsoupCrawler) : SternHafferlCollector(
    jsoupCrawler,
    VehicleType.TRAM,
    operatorId = "sternhafferl",
    locationId = "traunseetram",
    locationName = "Traunseetram Nostalgie",
    sourceUrl = "https://www.stern-verkehr.at/ausfluege-events/nostalgiefahrt-traunseetram/",
    eventJsonApiUrl = "https://www.stern-verkehr.at/wp-json/ausfluege-events/v1/events",
    filterIncludeStrings = listOf("traunsee", "traunseetram", "gmunden")
) {
    override fun collectEvents(): List<Event> = collectEventsFromPage(sourceUrl)

    override fun getName(): String = "Traunseetram Collector"
}