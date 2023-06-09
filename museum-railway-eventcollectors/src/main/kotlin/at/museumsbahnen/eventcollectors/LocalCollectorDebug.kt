package at.museumsbahnen.eventcollectors

import at.museumsbahnen.eventcollectors.collectors.OegegShopCollector
import events.boudicca.api.eventcollector.EventCollectorDebugger

fun main() {
    EventCollectorDebugger()
        .debug(OegegShopCollector())
}