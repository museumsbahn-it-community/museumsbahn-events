package at.museumsbahnevents.eventcollectors

import at.museumsbahnevents.eventcollectors.collectors.*
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