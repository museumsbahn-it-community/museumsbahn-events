package at.museumrailwayevents.eventcollectors

import at.museumrailwayevents.eventcollectors.collectors.*
import at.museumrailwayevents.eventcollectors.collectors.erzbergbahn.ErzbergbahnCollector
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.AtterseeSchifffahrtCollector
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.AtterseebahnCollector
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.TraunseetramCollector
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.eventcollectors.base.cache.FileBackedFetcherCache
import base.boudicca.SemanticKeys
import base.boudicca.api.eventcollector.config.EventCollectorBaseConfig
import base.boudicca.api.eventcollector.configuration.EventCollectorsConfigurationProperties
import base.boudicca.api.eventcollector.debugger.DataShouldContainKey
import base.boudicca.api.eventcollector.debugger.EventCollectorDebugger
import base.boudicca.api.eventcollector.debugger.ValidationSeverity
import base.boudicca.api.eventcollector.runner.buildRunnerFor
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import java.io.File

@Profile("debug")
@SpringBootApplication
class LocalCollectorDebug(
    val jsoupCrawler: JsoupCrawler,
    val configuration: EventCollectorsConfigurationProperties,
) : CommandLineRunner {
    private val debugRunner =
        buildRunnerFor(
            listOf(
                // Add or remove collectors here for debugging
                EbflCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("EBFL")),
                // Stern & Hafferl
                AtterseebahnCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Atterseebahn")),
                AtterseeSchifffahrtCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Attersee Schifffahrt")),
                TraunseetramCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Traunseetram")),
                // mainline
                OegegShopCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("OEGEG Shop")),
                OegegSchmalspurCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("OEGEG Schmalspur")),
                ProBahnVorarlbergCollector().withDebugConfig(EventCollectorBaseConfig("ProBahn Vorarlberg")),
                NostalgiebahnenKärntenCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Nostalgiebahnen Kärnten")),
                SteirischeEisenbahnfreundeCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Steirische Eisenbahnfreunde")),
                EbmSchwechatCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("EBM Schwechat")),
                OesekStrasshofCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("OESEK Strasshof")),
                RegiobahnCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Regiobahn")),
                // local railways
                ErzbergbahnCollector().withDebugConfig(EventCollectorBaseConfig("Erzbergbahn")),
                MLVZwettlCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("MLV Zwettl")),
                ReblausexpressCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Reblausexpress")),
                WaldviertelbahnCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Waldviertelbahn")),
                // narrow gauge
                RheinbähnleCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Rheinbähnle")),
                WälderbähnleCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Wälderbähnle")),
                WackelsteinexpressCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Wackelsteinexpress")),
                HoellentalbahnCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Höllentalbahn")),
                YbbstalbahnCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Ybbstalbahn")),
                Mh6Collector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Mh6")),
                // tram
                WienerTramwayMuseumCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Wiener Tramway Museum")),
                TramwaymuseumGrazCollector(jsoupCrawler).withDebugConfig(EventCollectorBaseConfig("Tramwaymuseum Graz")),
            ),
        ).withFetcherCache(FileBackedFetcherCache(File("./fetcher.cache"))).buildDebugRunner()

    @Bean
    fun eventCollectionRunner() = debugRunner.runner

    override fun run(vararg args: String) {
        EventCollectorDebugger(
            debugRunner,
            configuration,
            verboseDebugging = true,
            verboseValidation = true,
        ).validate(
            listOf(
                DataShouldContainKey(SemanticKeys.DESCRIPTION, ValidationSeverity.Error),
                DataShouldContainKey(SemanticKeys.PICTURE_URL, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.PICTURE_ALT_TEXT, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.TAGS, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.REGISTRATION, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.CATEGORY, ValidationSeverity.Warn),
                DataShouldContainKey(SemanticKeys.RECURRENCE_TYPE, ValidationSeverity.Warn),
            )
        ).runDebug()
    }
}

/**
 * Run this to test event collectors locally. Benefits:
 * 1) Starts the local web UI at http://localhost:8083/ to see collected events and errors
 * 2) Caches network calls in "fetcher.cache" - subsequent runs are much faster
 * 3) Allows enabling a remote or local enricher
 * 4) Allows ingesting events into a local EventDB
 *
 * Activate the "debug" Spring profile to use this app:
 *   --spring.profiles.active=debug
 */
fun main(args: Array<String>) {
    SpringApplicationBuilder(LocalCollectorDebug::class.java)
        .web(WebApplicationType.SERVLET)
        .build()
        .run(*args)
}
