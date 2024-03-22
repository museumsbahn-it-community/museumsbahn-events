package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import base.boudicca.SemanticKeys
import base.boudicca.model.Event

private val locationId_bahn = "nbik_eisenbahn"
private val locationId_schiff = "nbik_schiff"
private val locationId_historama = "nbik_historama"
private val locationId_museum = "nbik_verkehrsmuseum"
private val locationId_tram = "nbik_tram"

val operatorIdNbik = "nbik"

class NostalgiebahnenKärntenCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
        operatorId = operatorIdNbik,
        locationId = locationId_bahn,
        url = "https://www.nostalgiebahn.at/termine.html"
) {
    // these indicate durations and need special handling in the future
    val dateStringBlocklist = listOf(
            "ab",
            "bis"
    )

    override fun collectEvents(): List<Event> {
        return collectTerminseite(url)
    }


    private fun collectTerminseite(url: String): List<Event> {
        val document = jsoupCrawler.getDocument(url)
        val events = mutableListOf<Event>()

        val termine = document.select("div.terminseite.block")
        termine.forEach { entry ->
            val dateString = entry.selectFirst("h2")?.text()?.lowercase()
            val name = entry.selectFirst("h3")?.text()
            val imageUrl = entry.selectFirst("img")?.attr("src")

            if (dateString == null || name == null || dateStringBlocklist.any { dateString.contains(it) }) {
                return@forEach
            }

            val dates = DateParser.parseAllDatesFrom(dateString)
            var description = entry.select("div.textcontainer").filter { elem -> elem.children().select("h3").size > 0 }.first.text()

            // TODO: check if datestring has one of the blacklisted keywords

            description = description.replace(name, "").replace("Mehr erfahren?", "").trim()

            val additionalData = mutableMapOf<String, String>()
            additionalData[SemanticKeys.DESCRIPTION] = description

            if (imageUrl != null) {
                additionalData[SemanticKeys.PICTUREURL] = imageUrl
            }

            dates.forEach { date ->
                events.add(
                        createEvent(
                                name,
                                date,
                                additionalData
                        )
                )
            }
        }

        return events
    }

    override fun getName(): String = "Nostalgiebahnen in Kärnten"
}