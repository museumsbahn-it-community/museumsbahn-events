package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

val fahrplanUrl = "https://waelderbaehnle.at/fahrplanbetrieb-preise-2025"
val sonderfahrtenUrl = "https://waelderbaehnle.at/aktuelles/veranstaltungskalender-2025"

class WälderbähnleCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "waelderbaehnle",
    locationId = "waelderbaehnle",
    locationName = "Bregenzerwaldbahn Museumsbahn",
    sourceUrl = fahrplanUrl,
) {
    val locale = Locale.GERMAN
    val dateFormatter = getWaelderbaehnleDateFormatter(locale)
    val timeFormatter = getWaelderbaehnleTimeFormatter(locale)
    val offset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())

    data class DepartureData(val startTime: OffsetDateTime, val station: String, val traction: String)

    override fun collectEvents(): List<Event> {
        val fahrplan = collectFahrplanEvents()
        val sonderfahrten = collectSonderfahrten()

        return fahrplan + sonderfahrten
    }

    private fun collectFahrplanEvents(): MutableList<Event> {
        // page is named 2022, but is actually 2023
        // 2024-01-02: ...and 2024 as well ;)
        // 2024-01-21: oh damn! They renamed it. (╯°□°）╯︵ ┻━┻
        // 2025-01-15: they renamed it again this year, at least they are consistent
        //             but I hope the Veranstaltungskalender will be published under the right url
        val document = jsoupCrawler.getDocument(fahrplanUrl)

        val timetables = document.select("div.timetable-wrapper")
        val departures = mutableListOf<DepartureData>()

        timetables.forEach { timetable ->
            val date = timetable.getDate().first()
            val departuresSteam = timetable.select("tr.traction-1")
            val departuresDiesel = timetable.select("tr.traction-2")

            getDepartures(departuresSteam, date, departures, "steam")
            getDepartures(departuresDiesel, date, departures, "diesel")
        }

        val events = mutableListOf<Event>()
        departures
            .groupBy { it.startTime.toLocalDate() }
            .forEach { operatingDay ->
                val eventTitle = "Bregenzerwaldbahn"
                val departuresOnDay = operatingDay.value.sortedBy { it.startTime }
                val firstDeparture = departuresOnDay.first()

                var description = """
                    Fahrtag auf der Bregenzerwaldbahn.
                    
                    Fahrplan:
                                 
                """.trimIndent()
                val departureTimeFormat = DateTimeFormatter.ofPattern("HH:mm")
                val departureList =
                    departuresOnDay.joinToString("\n") { "${it.startTime.format(departureTimeFormat)} ab ${it.station}" }

                val additionalData = mutableMapOf(
                    CommonKeys.VEHICLE_TYPE to departures.first().traction,
                    CommonKeys.DESCRIPTION to "${description}${departureList}",
                    SemanticKeys.REGISTRATION to Registration.RESERVATION_RECOMMENDED,
                    SemanticKeys.URL to fahrplanUrl,
                    SemanticKeys.CATEGORY to Category.MUSEUM_RAILWAY,
                    SemanticKeys.TAGS to (TAGS_MUSEUM_RAILWAY_OPERATING + TAGS_NARROW_GAUGE).toTagsValue()
                )

                events.add(createEvent(eventTitle, firstDeparture.startTime, fahrplanUrl, additionalData))
            }
        return events
    }

    private fun collectSonderfahrten(): List<Event> {
        val document = jsoupCrawler.getDocument(sonderfahrtenUrl)
        val events = mutableListOf<Event>()

        val article = document.select("article")
        val imageUrl = article.select("img").attr("src")
        val eventBoxes = article.select("p")
        eventBoxes.forEach { box ->
            val title = box.select("em").text()
            // TODO: properly handle durations
            val dates = DateParser.parseAllDatesFrom(title)
            val name = DateParser.removeDateAndTimeFrom(title)
            val description = DateParser.removeDateAndTimeFrom(box.text())

            dates.forEach { date ->
                events.add(
                    createEvent(
                        name,
                        date,
                        sonderfahrtenUrl,
                        mutableMapOf(
                            SemanticKeys.CATEGORY to Category.SPECIAL_TRIP,
                            SemanticKeys.REGISTRATION to Registration.TICKET,
                            SemanticKeys.PICTURE_URL to imageUrl,
                            SemanticKeys.DESCRIPTION to description,
                            SemanticKeys.URL to sonderfahrtenUrl,
                            SemanticKeys.TAGS to (TAGS_MUSEUM_EVENT + TAGS_NARROW_GAUGE).toTagsValue()
                        )
                    )
                )
            }
        }

        return events
    }

    private fun getDepartures(
        departuresSteam: Elements,
        date: LocalDate,
        departures: MutableList<DepartureData>,
        tractionName: String,
    ) {
        departuresSteam.forEach { departure ->
            val station = departure.getStation()
            if (station == null) {
                println("warning station is null")
                return@forEach
            }
            val departureTime = departure.getDepartureTime()
            if (departureTime == null) {
                println("warning departure time is null")
                return@forEach
            }

            val startTime = OffsetDateTime.of(
                date, departureTime, offset
            )

            departures.add(DepartureData(startTime, station, tractionName))
        }
    }

    fun getWaelderbaehnleDateFormatter(locale: Locale): DateTimeFormatter {
        val builder = DateTimeFormatterBuilder()
        builder.parseCaseInsensitive()
        builder.appendPattern("dd.MM.yyyy")
        return builder.toFormatter(locale)
    }

    fun getWaelderbaehnleTimeFormatter(locale: Locale): DateTimeFormatter {
        val builder = DateTimeFormatterBuilder()
        builder.parseCaseInsensitive()
        builder.appendPattern("HH:mm")
        return builder.toFormatter(locale)
    }

    override fun getName(): String = "Wälderbähnle/Bregenzerwaldbahn Collector"

    private fun Element.getDate(): List<LocalDate> {
        return this.select("tr.day>th").eachText().map { LocalDate.from(dateFormatter.parse(it.split(" ")[1])) }
    }

    private fun Element.getDepartureTime(): LocalTime? {
        val data = this.select("td")
        if (data.size < 2) {
            return null
        }
        return LocalTime.from(timeFormatter.parse(data[1].text()))
    }
}

private fun Element.getStation(): String? = this.select("td").first()?.text()



