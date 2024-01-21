package at.museumsbahnevents.eventcollectors

import at.museumsbahnevents.eventcollectors.collectors.WälderbähnleCollector
import base.boudicca.api.eventcollector.EventCollectorDebugger

fun main() {
    EventCollectorDebugger()
        .debug(WälderbähnleCollector())
}