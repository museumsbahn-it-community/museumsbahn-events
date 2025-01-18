package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.keepLineBreaks
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import org.jsoup.nodes.Element

class MLVZwettlCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    "mlv_zwettl",
    "mlv_zwettl",
    "http://www.lokalbahnverein.at/deutsch/event.php",
    locationName = "Museums-Lokalbahnverein Zwettl"
) {
    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(sourceUrl)
            .keepLineBreaks(
            after = listOf("tr", "h3")
        )
        val events = mutableListOf<Event>()

        val individualEventLines = mutableListOf<List<Element>>()
        var currentEventLines = mutableListOf<Element>()
        val mainTable =
            document.select("tr:contains(Veranstaltungen)").filter { node -> node.select("h1").isNotEmpty() }.first()
        val rows = mainTable.children().select("tr")

        rows.forEach { row ->
            if (row.select("h2").isNotEmpty()) {
                if (currentEventLines.isNotEmpty()) {
                    individualEventLines.add(currentEventLines)
                    currentEventLines = mutableListOf();
                }
            }
            currentEventLines.add(row)
        }
        if (currentEventLines.isNotEmpty()) {
            individualEventLines.add(currentEventLines)
        }
        individualEventLines.forEach { eventLines ->
            val name = eventLines.first().select("h2").text()
            val dateText = eventLines.first().text()
            val hasDate = DateParser.fullDateRegex.containsMatchIn(dateText)
            val eventImage = eventLines.first().select("img").attr("src")
            var category = MuseumEventsCategory.MUSEUM_RAILWAY.jsonValue
            var locomotiveType: String? = null

            if (eventImage.isMuseum()) {
                category = MuseumEventsCategory.RAILWAY_MUSEUM.jsonValue
            }

            if (eventImage.isSteam()) {
                locomotiveType = VehicleType.STEAM_TRAIN
            }

            if (eventImage.isDiesel()) {
                locomotiveType = VehicleType.DIESEL_TRAIN
            }

            if (!hasDate) {
                return@forEach
            }

            val dates = DateParser.parseAllDatesFrom(dateText)
            val time = DateParser.parseAllTimesFrom(dateText).firstOrNull()

            val descriptionLines = mutableListOf<String>()
            descriptionLines.addAll(
                eventLines.subList(1, eventLines.size)
                    .map { it.text() }
            )
            val description = descriptionLines.joinToString("\n")

            dates.forEach { date ->
                val eventStart = if (time == null) {
                    date
                } else {
                    date.plusHours(time.hour.toLong()).plusMinutes(time.minute.toLong())
                }

                val isAbgesagt = description.replace(Regex("\\s"), "").contains("abgesagt")

                val eventName = if (isAbgesagt) {
                    "ABGESAGT - $name"
                } else {
                    name
                }

                val additionalData = mutableMapOf(
                    SemanticKeys.TAGS to TAGS_MUSEUM_RAILWAY_SPECIAL_TRIP.toTagsValue(),
                    SemanticKeys.DESCRIPTION to description,
                    SemanticKeys.CATEGORY to category,
                    SemanticKeys.REGISTRATION to MuseumEventRegistration.TICKET.jsonValue,
                )

                if (locomotiveType != null) {
                    additionalData[CommonKeys.VEHICLE_TYPE] = locomotiveType
                }

                events.add(
                    createEvent(
                        eventName,
                        eventStart,
                        sourceUrl,
                        additionalData,
                        locationId
                    )
                )
            }
        }
        return events
    }


    override fun getName(): String = "MLV Zwettl Collector"
}

private fun String.isMuseum(): Boolean = this.contains("eventshed.gif") || this.contains("eventshed_no.gif")
private fun String.isSteam(): Boolean = this.contains("eventlok.gif") ||  this.contains("eventlok_no.gif")
private fun String.isDiesel(): Boolean = this.contains("eventdiesel.gif") || this.contains("eventdiesel_no.gif")
