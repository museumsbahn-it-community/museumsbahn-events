package at.museumrailwayevents.eventcollectors

import at.museumrailwayevents.eventcollectors.collectors.*
import at.museumrailwayevents.eventcollectors.collectors.erzbergbahn.ErzbergbahnCollector
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.AtterseeSchifffahrtCollector
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.AtterseebahnCollector
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.TraunseetramCollector
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.eventcollectors.base.cache.FileBackedFetcherCache
import base.boudicca.SemanticKeys
import base.boudicca.api.eventcollector.EventCollector
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

/**
 * Configures [this] collector from its matching entry in `application.yml`
 * (matched by [EventCollector.defaultDisplayName], which - per the vendored base class - returns the
 * `@BoudiccaEventCollector` annotation's `collectorTypeName`, i.e. the yaml `type:` value).
 *
 * This replaces per-collector `.withDebugConfig(EventCollectorBaseConfig("..."))` literals: debug mode
 * now binds through the exact same `Binder`-based `configure()` path production uses, so URLs/config
 * only ever have to be declared once, in yaml - regardless of how many collectors get their own typed
 * config class over the following phases of the eventcollector-overhaul campaign.
 */
private fun <T : EventCollectorBaseConfig> EventCollector<T>.configureFromYaml(
    configuration: EventCollectorsConfigurationProperties,
): EventCollector<T> {
    val type = defaultDisplayName()
    val yamlConfig = configuration.collectors.find { it.type == type }
    configure(yamlConfig?.name, yamlConfig?.properties ?: emptyMap())
    return this
}

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
                EbflCollector(jsoupCrawler).configureFromYaml(configuration),
                // Stern & Hafferl
                AtterseebahnCollector(jsoupCrawler).configureFromYaml(configuration),
                AtterseeSchifffahrtCollector(jsoupCrawler).configureFromYaml(configuration),
                TraunseetramCollector(jsoupCrawler).configureFromYaml(configuration),
                // mainline
                OegegShopCollector(jsoupCrawler).configureFromYaml(configuration),
                OegegSchmalspurCollector(jsoupCrawler).configureFromYaml(configuration),
                ProBahnVorarlbergCollector().configureFromYaml(configuration),
                NostalgiebahnenKärntenCollector(jsoupCrawler).configureFromYaml(configuration),
                SteirischeEisenbahnfreundeCollector(jsoupCrawler).configureFromYaml(configuration),
                EbmSchwechatCollector(jsoupCrawler).configureFromYaml(configuration),
                OesekStrasshofCollector(jsoupCrawler).configureFromYaml(configuration),
                RegiobahnCollector(jsoupCrawler).configureFromYaml(configuration),
                // local railways
                ErzbergbahnCollector().configureFromYaml(configuration),
                MLVZwettlCollector(jsoupCrawler).configureFromYaml(configuration),
                ReblausexpressCollector(jsoupCrawler).configureFromYaml(configuration),
                WaldviertelbahnCollector(jsoupCrawler).configureFromYaml(configuration),
                // narrow gauge
                RheinbähnleCollector(jsoupCrawler).configureFromYaml(configuration),
                WälderbähnleCollector(jsoupCrawler).configureFromYaml(configuration),
                WackelsteinexpressCollector(jsoupCrawler).configureFromYaml(configuration),
                HoellentalbahnCollector(jsoupCrawler).configureFromYaml(configuration),
                YbbstalbahnCollector(jsoupCrawler).configureFromYaml(configuration),
                Mh6Collector(jsoupCrawler).configureFromYaml(configuration),
                // tram
                WienerTramwayMuseumCollector(jsoupCrawler).configureFromYaml(configuration),
                TramwaymuseumGrazCollector(jsoupCrawler).configureFromYaml(configuration),
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
