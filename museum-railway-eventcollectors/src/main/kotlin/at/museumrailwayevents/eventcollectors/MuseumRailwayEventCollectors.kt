package at.museumrailwayevents.eventcollectors

import AtterseeSchifffahrtCollector
import at.museumrailwayevents.eventcollectors.collectors.*
import at.museumrailwayevents.eventcollectors.service.JsoupCrawlerImpl
import base.boudicca.api.eventcollector.EventCollectorCoordinatorBuilder

val crawler = JsoupCrawlerImpl()
fun main() {
    EventCollectorCoordinatorBuilder()
        .addEventCollector(AtterseebahnCollector(crawler))
        .addEventCollector(AtterseeSchifffahrtCollector(crawler))
        .addEventCollector(EbflCollector(crawler))
        .addEventCollector(OegegShopCollector(crawler))
        .addEventCollector(OegegSchmalspurCollector(crawler))
        .addEventCollector(ProBahnVorarlbergCollector())
        .addEventCollector(RheinbähnleCollector(crawler))
        .addEventCollector(WälderbähnleCollector(crawler))
        .addEventCollector(NostalgiebahnenKärntenCollector(crawler))
        .addEventCollector(WackelsteinexpressCollector(crawler))
        .addEventCollector(HoellentalbahnCollector(crawler))
        .addEventCollector(YbbstalbahnCollector(crawler))
        .addEventCollector(Mh6Collector(crawler))
        .addEventCollector(WaldviertelbahnCollector(crawler))
        .addEventCollector(ReblausexpressCollector(crawler))
        .addEventCollector(SteirischeEisenbahnfreundeCollector(crawler))
        .addEventCollector(EbmSchwechatCollector(crawler))
        .addEventCollector(WienerTramwayMuseumCollector(crawler))
        .addEventCollector(TraunseetramCollector(crawler))
        .addEventCollector(TramwaymuseumGrazCollector(crawler))
        .addEventCollector(MLVZwettlCollector(crawler))
        .build()
        .startWebUi()
        .run()
}