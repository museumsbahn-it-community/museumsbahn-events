package at.museumsbahnen.eventcollectors.collectors

import events.boudicca.api.eventcollector.Event
import events.boudicca.api.eventcollector.EventCollector
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class WälderbähnleCollector : EventCollector {
    val locale = Locale.GERMAN
    val dateFormatter = getWaelderbaehnleDateFormatter(locale)
    val timeFormatter = getWaelderbaehnleTimeFormatter(locale)
    val offset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())

    data class DepartureData(val startTime: OffsetDateTime, val station: String, val traction: String)

    override fun collectEvents(): List<Event> {
        // page is named 2022, but is actually 2023
        val document = Jsoup.connect("https://waelderbaehnle.at/fahrplanbetrieb-preise-2022").get()

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
        departures.forEach { departure ->
            val eventTitle = "Sonderfahrt Wälderbähnle"
            val additionalData = mutableMapOf<String, String>()
            additionalData["station"] = departure.station
            additionalData["traction"] = departure.traction

            events.add(Event(eventTitle, departure.startTime, additionalData))
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



