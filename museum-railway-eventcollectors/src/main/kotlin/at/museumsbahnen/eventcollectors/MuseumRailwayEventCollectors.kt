package at.museumsbahnen.eventcollectors

import at.museumsbahnen.eventcollectors.collectors.*
import base.boudicca.api.eventcollector.EventCollectorScheduler

fun main() {
    EventCollectorScheduler()
        .addEventCollector(OegegShopCollector())
        .addEventCollector(OegegSchmalspurCollector())
        .addEventCollector(ProBahnVorarlbergCollector())
        .addEventCollector(RheinbähnleCollector())
        .addEventCollector(WälderbähnleCollector())
        .run()
}