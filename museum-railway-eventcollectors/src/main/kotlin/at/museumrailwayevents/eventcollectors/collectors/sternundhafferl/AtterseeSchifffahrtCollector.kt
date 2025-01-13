package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.VehicleType
import base.boudicca.model.Event

class AtterseeSchifffahrtCollector(jsoupCrawler: JsoupCrawler) : SternHafferlCollector(
    jsoupCrawler,
    VehicleType.SHIP,
    operatorId = "sth",
    locationId = "atterseeschiff",
    locationName = "Atterseeschifffahrt Nostalgie",
    sourceUrl = "https://atterseeschifffahrt.at/ausfluege-events/nostalgiezug-schiff/",
    eventJsonApiUrl = "https://atterseeschifffahrt.at/wp-json/ausfluege-events/v1/events"
) {
    override fun collectEvents(): List<Event> = collectEventsFromPage(sourceUrl)

    override fun getName(): String = "Atterseeschifffahrt Collector"
}