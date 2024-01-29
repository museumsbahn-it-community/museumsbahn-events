package at.museumrailwayevents.eventcollectors

import at.museumrailwayevents.eventcollectors.collectors.*
import base.boudicca.api.eventcollector.EventCollectorScheduler

fun main() {
    EventCollectorScheduler()
        .addEventCollector(OegegShopCollector())
        .addEventCollector(OegegSchmalspurCollector())
        .addEventCollector(ProBahnVorarlbergCollector())
        .addEventCollector(RheinbähnleCollector())
        .addEventCollector(WälderbähnleCollector())
        .startWebUi()
        .run()
}