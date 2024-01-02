package at.museumsbahnevents.eventcollectors.collectors

import at.museumsbahnevents.api.model.conventions.CommonKeys
import base.boudicca.model.Event
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class WälderbähnleCollector : MuseumRailwayEventCollector(
    operatorId = "waelderbaehnle",
    locationId = "waelderbaehnle",
    url = "https://waelderbaehnle.at/fahrplanbetrieb-preise-2022",
) {
    val locale = Locale.GERMAN
    val dateFormatter = getWaelderbaehnleDateFormatter(locale)
    val timeFormatter = getWaelderbaehnleTimeFormatter(locale)
    val offset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())

    data class DepartureData(val startTime: OffsetDateTime, val station: String, val traction: String)

    override fun collectEvents(): List<Event> {
        // page is named 2022, but is actually 2023
        // 2024-01-02: ...and 2024 as well ;)
        val document = Jsoup.connect(url).get()

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
            val eventTitle = "Fahrbetrieb Bregenzerwaldbahn"
            val additionalData = mutableMapOf<String, String>()
            val departuresOnDay = operatingDay.value.sortedBy { it.startTime }
            val firstDeparture = departuresOnDay.first

            var description = """
                Fahrtag auf der Bregenzerwaldbahn.
                
                Fahrplan:
                             
            """.trimIndent()
                val departureTimeFormat = DateTimeFormatter.ofPattern("HH:mm")
                val departureList = departuresOnDay.map { "${it.startTime.format(departureTimeFormat)} ab ${it.station}" }.joinToString("\n")

            additionalData[CommonKeys.LOCOMOTIVE_TYPE] = departures.first.traction
            additionalData[CommonKeys.DESCRIPTION] = "${description}${departureList}"

            events.add(createEvent(eventTitle, firstDeparture.startTime, additionalData))
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



