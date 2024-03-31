package at.museumrailwayevents.eventcollectors

import at.museumrailwayevents.eventcollectors.collectors.*
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.eventcollectors.service.JsoupCrawlerImpl
import base.boudicca.api.eventcollector.EventCollectorScheduler

val crawler = JsoupCrawlerImpl()
fun main() {
    EventCollectorScheduler()
        .addEventCollector(OegegShopCollector(crawler))
        .addEventCollector(OegegSchmalspurCollector(crawler))
        .addEventCollector(ProBahnVorarlbergCollector())
        .addEventCollector(RheinbähnleCollector(crawler))
        .addEventCollector(WälderbähnleCollector(crawler))
        .addEventCollector(NostalgiebahnenKärntenCollector(crawler))
        .addEventCollector(WackelsteinexpressCollector(crawler))
        .addEventCollector(HoellentalbahnCollector(crawler))
        .addEventCollector(YbbstalbahnCollector(crawler))
        .addEventCollector(EbflCollector(crawler))
        .addEventCollector(Mh6Collector(crawler))
        .addEventCollector(WaldviertelbahnCollector(crawler))
        .startWebUi()
        .run()
}