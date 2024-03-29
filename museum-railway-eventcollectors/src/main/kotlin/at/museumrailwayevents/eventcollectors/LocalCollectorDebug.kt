package at.museumrailwayevents.eventcollectors

import at.museumrailwayevents.eventcollectors.collectors.ProBahnVorarlbergCollector
import at.museumrailwayevents.eventcollectors.collectors.WälderbähnleCollector
import at.museumrailwayevents.eventcollectors.service.JsoupCrawlerImpl
import base.boudicca.SemanticKeys
import base.boudicca.api.eventcollector.debugger.DataShouldContainKey
import base.boudicca.api.eventcollector.debugger.EventCollectorDebugger
import base.boudicca.api.eventcollector.debugger.ValidationSeverity

fun main() {
    EventCollectorDebugger(verboseDebugging = false, keepOpen = false)
        .debug(ProBahnVorarlbergCollector())
//        .debug(WälderbähnleCollector(JsoupCrawlerImpl()))
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