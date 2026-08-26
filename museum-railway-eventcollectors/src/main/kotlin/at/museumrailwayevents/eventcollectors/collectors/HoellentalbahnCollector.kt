package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.VehicleType
import base.boudicca.api.eventcollector.annotations.BoudiccaEventCollector
import base.boudicca.model.Event

// NOTE: unlike YbbstalbahnCollector, this collector is NOT yet config-migrated this phase
// (Höllentalbahn is one of the zero-event collectors tracked by its own intake ticket - see
// .planning/intake/zero-events-*.md - and gets its full config/URL-externalization pass there).
// These `override val`s just keep it compiling against the now-generic MuseumRailwayEventCollector<T>
// base with byte-for-byte the same values it hardcoded before.
@BoudiccaEventCollector(collectorTypeName = "hoellentalbahn")
class HoellentalbahnCollector(jsoupCrawler: JsoupCrawler) : OeglbCollector(jsoupCrawler) {

    override val operatorId = "oeglb"
    override val locationId = "hoellentalbahn"
    override val locationName = "Höllentalbahn"
    override val sourceUrl = "https://www.lokalbahnen.at/hoellentalbahn/"
    override val locomotiveType = VehicleType.ELECTRIC_TRAIN

    private val baseUrl = "https://www.lokalbahnen.at/"

    private val fahrplanUrls = listOf(
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/fahrplan/fahrplan-sommer/",
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/fahrplan/fahrplan-herbst/"
    )

    private val sonderfahrtenUrl = "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/sonderveranstaltungen/"

    override fun collectEvents(): List<Event> {
        val regularEvents = fahrplanUrls.flatMap { collectFahrplanPage(it) }
        val sonderfahrten = collectSonderfahrten(baseUrl, sonderfahrtenUrl)
        return regularEvents + sonderfahrten
    }

    override fun defaultDisplayName(): String = "Höllentalbahn Collector"

}
