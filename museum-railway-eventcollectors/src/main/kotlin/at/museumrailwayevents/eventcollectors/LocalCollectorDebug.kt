package at.museumrailwayevents.eventcollectors

import at.museumrailwayevents.eventcollectors.collectors.*
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.AtterseeSchifffahrtCollector
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.AtterseebahnCollector
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.TraunseetramCollector
import base.boudicca.SemanticKeys
import base.boudicca.api.eventcollector.debugger.DataShouldContainKey
import base.boudicca.api.eventcollector.debugger.EventCollectorDebugger
import base.boudicca.api.eventcollector.debugger.ValidationSeverity

fun main() {
    EventCollectorDebugger(verboseDebugging = true, keepOpen = false)
        // Stern & Hafferl
//        .debug(AtterseebahnCollector(crawler))
//        .debug(AtterseeSchifffahrtCollector(crawler))
//        .debug(TraunseetramCollector(crawler))
        // mainline
//        .debug(EbflCollector(crawler)) // error
//        .debug(OegegShopCollector(crawler))
//        .debug(ProBahnVorarlbergCollector())
//        .debug(NostalgiebahnenKärntenCollector(crawler))
//        .debug(SteirischeEisenbahnfreundeCollector(crawler))
//        .debug(EbmSchwechatCollector(crawler))
        .debug(OesekStrasshofCollector(crawler))
        // local railways
//        .debug(ErzbergbahnCollector())
//        .debug(ReblausexpressCollector(crawler))
//        .debug(MLVZwettlCollector(crawler))
        // narrow gauge
//        .debug(OegegSchmalspurCollector(crawler))
//        .debug(RheinbähnleCollector(crawler))
//        .debug(WälderbähnleCollector(crawler))
//        .debug(WackelsteinexpressCollector(crawler)) // error
//        .debug(HoellentalbahnCollector(crawler))
//        .debug(YbbstalbahnCollector(crawler))
//        .debug(Mh6Collector(crawler))
//        .debug(WaldviertelbahnCollector(crawler))
        // tram
//        .debug(WienerTramwayMuseumCollector(crawler))
//        .debug(TramwaymuseumGrazCollector(crawler))
        .validate(
            validations = listOf(
                DataShouldContainKey(SemanticKeys.DESCRIPTION, ValidationSeverity.Error),
                DataShouldContainKey(SemanticKeys.PICTURE_URL, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.PICTURE_ALT_TEXT, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.TAGS, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.REGISTRATION, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.CATEGORY, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.RECURRENCE_TYPE, ValidationSeverity.Warn),
            )
        )
}