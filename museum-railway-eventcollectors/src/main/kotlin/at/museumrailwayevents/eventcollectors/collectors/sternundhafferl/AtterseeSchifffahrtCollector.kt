package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.Tags
import base.boudicca.model.Event

class AtterseeSchifffahrtCollector(jsoupCrawler: JsoupCrawler) : SternHafferlCollector(
    jsoupCrawler,
    Tags.LOCOMOTIVE_TYPE_TRAM,
    operatorId = "sth",
    locationId = "atterseeschiff",
    locationName = "Atterseeschifffahrt Nostalgie",
    url = "https://atterseeschifffahrt.at/ausfluege-events/nostalgiezug-schiff/",
    eventJsonApiUrl = "https://atterseeschifffahrt.at/wp-json/ausfluege-events/v1/events"
) {
    override fun collectEvents(): List<Event> = collectEventsFromPage(url)

    override fun getName(): String = "Atterseeschifffahrt Collector"
}