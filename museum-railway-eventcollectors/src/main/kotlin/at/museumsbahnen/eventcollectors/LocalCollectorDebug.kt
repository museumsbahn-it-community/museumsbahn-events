package at.museumsbahnen.eventcollectors

import at.museumsbahnen.eventcollectors.collectors.ProBahnVorarlbergCollector
import at.museumsbahnen.eventcollectors.collectors.RheinbähnleCollector
import events.boudicca.api.eventcollector.EventCollectorDebugger

fun main() {
    EventCollectorDebugger()
        .debug(ProBahnVorarlbergCollector())
}