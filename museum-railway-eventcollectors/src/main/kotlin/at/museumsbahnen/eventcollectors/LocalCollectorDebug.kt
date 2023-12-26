package at.museumsbahnen.eventcollectors

import at.museumsbahnen.eventcollectors.collectors.WälderbähnleCollector
import base.boudicca.api.eventcollector.EventCollectorDebugger

fun main() {
    EventCollectorDebugger()
        .debug(WälderbähnleCollector())
}