package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import base.boudicca.api.eventcollector.annotations.BoudiccaEventCollector
import base.boudicca.model.Event

@BoudiccaEventCollector(collectorTypeName = "ybbstalbahn")
class YbbstalbahnCollector(jsoupCrawler: JsoupCrawler) : OeglbCollector(jsoupCrawler) {

    override fun collectEvents(): List<Event> {
        val regularEvents = config.fahrplanUrls.flatMap { collectFahrplanPage(it) }
        val sonderfahrten = collectSonderfahrten(config.baseUrl, config.sonderfahrtenUrl)
        return regularEvents + sonderfahrten
    }

    override fun defaultDisplayName(): String = "Ybbstalbahn Collector"

}
