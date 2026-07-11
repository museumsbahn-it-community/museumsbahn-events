package at.museumrailwayevents.eventcollectors.collectors.erzbergbahn

import base.boudicca.api.eventcollector.annotations.BoudiccaEventCollector
import base.boudicca.model.Event


@BoudiccaEventCollector(collectorTypeName = "erzbergbahn")
class ErzbergbahnCollector : RegiondoCollector(
    locationName = "Erzbergbahn",
    sourceUrl = "https://www.erzbergbahn.at/",
    locationId = "erzbergbahn",
    operatorId = "erzbergbahn",
    eventOverviewUrl = "https://www.erzbergbahn.at/bahnfahrten-angebote/",
    regiondoPartnerCode = "VE187"
) {
    override fun getName(): String = "Erzbergbahn Collector"
}