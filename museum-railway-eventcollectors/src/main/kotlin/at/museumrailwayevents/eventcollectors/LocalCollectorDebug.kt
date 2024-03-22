package at.museumrailwayevents.eventcollectors

import at.museumrailwayevents.eventcollectors.collectors.WälderbähnleCollector
import at.museumrailwayevents.eventcollectors.service.JsoupCrawlerImpl
import base.boudicca.api.eventcollector.EventCollectorDebugger

fun main() {
    EventCollectorDebugger()
        .debug(WälderbähnleCollector(JsoupCrawlerImpl()))
}