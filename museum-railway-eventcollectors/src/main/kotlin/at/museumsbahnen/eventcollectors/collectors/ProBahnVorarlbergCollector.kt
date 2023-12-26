package at.museumsbahnen.eventcollectors.collectors

import base.boudicca.api.eventcollector.EventCollector
import base.boudicca.model.Event
import org.jsoup.Jsoup
import java.time.*
import java.util.*

class ProBahnVorarlbergCollector : EventCollector {
        val locale = Locale.GERMAN
//        val dateFormatter = (locale)
//        val timeFormatter = getWaelderbaehnleTimeFormatter(locale)
        val offset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())
    override fun collectEvents(): List<Event> {

        val document = Jsoup.connect("https://probahn-vlbg.at/sonderfahrten/liste/").get()

        val events = document.select("article")
        val departures = mutableListOf<WälderbähnleCollector.DepartureData>()

        events.forEach { event ->
            val titleLink = event.select("h3 > a")
            val title = titleLink.attr("title")
            val link = titleLink.attr("href")
        }

        //println(timetables)


        return emptyList()
    }

    override fun getName(): String = "ProBahn Vorarlberg Collector"
}