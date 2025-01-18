package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event

class SteirischeEisenbahnfreundeCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    "stef",
    "stef",
    "https://www.stef.at/program/",
    locationName = "Steirische Eisenbahnfreunde"
) {
    private val locationIdMuseum = "stef_museum"
    private val locationIdSonderfahrten = "stef_sonderfahrten"

    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(sourceUrl);
        val eventList = document.select("div.q_list")
        val lines = eventList.select("p").eachText()

        val individualEventLines = mutableListOf<List<String>>()
        var currentEventLines = mutableListOf<String>()

        val events = mutableListOf<Event>();

        lines.forEach { line ->
            if (line.contains(DateParser.fullDateRegex)) {
                if (currentEventLines.isNotEmpty()) {
                    individualEventLines.add(currentEventLines)
                    currentEventLines = mutableListOf();
                }
            }
            if (line.isNotBlank() && line.replace(".", "").isNotBlank()) {
                currentEventLines.add(line)
            }
        }
        if (currentEventLines.isNotEmpty()) {
            individualEventLines.add(currentEventLines)
        }

        individualEventLines.forEach { eventLines ->
            val headingParts = eventLines.first().split("–").map { it.trim() }
            val date = DateParser.parseDate(headingParts[0])
            val name = "${locationName} - ${headingParts[1]}"

            val descriptionLines = mutableListOf<String>()
            descriptionLines.addAll(eventLines.subList(1, eventLines.size))
            val description = descriptionLines.joinToString("\n")

            if ((name.isSonderzug() || description.isSonderzug()) && isPublicEvent(name, description)) {
                events.add(
                    createEvent(
                        name,
                        date,
                        sourceUrl,
                        mutableMapOf(
                            SemanticKeys.TAGS to TAGS_MUSEUM_RAILWAY_SPECIAL_TRIP.toTagsValue(),
                            SemanticKeys.DESCRIPTION to description,
                            SemanticKeys.CATEGORY to MuseumEventsCategory.SPECIAL_TRIP.jsonValue,
                            SemanticKeys.REGISTRATION to MuseumEventRegistration.TICKET.jsonValue,
                        ),
                        locationIdSonderfahrten
                    )
                )
            } else if(isPublicEvent(name, description)) {
                // we ignore the non-public events for now as we do not have a good way of handling these in boudicca context
                events.add(
                    createEvent(
                        name,
                        date,
                        sourceUrl,
                        mutableMapOf(
                            SemanticKeys.TAGS to TAGS_MUSEUM_EVENT.toTagsValue(),
                            SemanticKeys.DESCRIPTION to description,
                            SemanticKeys.CATEGORY to MuseumEventsCategory.RAILWAY_MUSEUM.jsonValue,
                            SemanticKeys.REGISTRATION to MuseumEventRegistration.TICKET.jsonValue,
                        ),
                        locationIdSonderfahrten
                    )
                )
            }
        }

        return events
    }

    private fun isPublicEvent(name: String, description: String) = !name.isPrivateEvent() && !description.isPrivateEvent()

    private fun String.isSonderzug() = this.lowercase().contains("sonderzug") || this.lowercase().contains("sonderfahrt")
    private fun String.isPrivateEvent() = this.lowercase().contains("geschlossene veranstaltung") ||
            this.lowercase().contains("nicht öffentlich")

    override fun getName(): String = "Steirische Eisenbahnfreunde Collector"
}