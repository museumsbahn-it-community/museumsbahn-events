package at.museumsbahnen.eventcollectors

import at.museumsbahnen.eventcollectors.collectors.OegegShopCollector
import events.boudicca.api.eventcollector.EventCollectorScheduler

fun main() {
    fun main() {
        EventCollectorScheduler()
            .addEventCollector(OegegShopCollector())
            .run()
    }
}