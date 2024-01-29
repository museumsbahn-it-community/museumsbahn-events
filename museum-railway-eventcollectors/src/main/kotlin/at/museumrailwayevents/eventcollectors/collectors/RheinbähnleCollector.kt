package at.museumrailwayevents.eventcollectors.collectors

import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import org.jsoup.Jsoup
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RheinbähnleCollector : MuseumRailwayEventCollector(
    operatorId = "rheinschauen",
    locationId = "rheinschauen",
    url = "https://www.rheinschauen.at/museum-baehnle/fahrplan"
) {
    val beginString = "Beginn der Veranstaltung"
    val endString = "Ende der Veranstaltung"
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy [HH:mm][H:mm]")
    val offset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())
    val baseUrl = "https://www.rheinschauen.at/"

    override fun collectEvents(): List<Event> {
        val document = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
            .header(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
            )
            .get()
        val eventEntries = document.select("div.eb-event")

        val events = mutableListOf<Event>()

        eventEntries.forEach { event ->
            val title = event.select("h2.eb-event-title").eachText().first()
            val description = event.select("div.eb-description-details > p").eachText().first()
            val link = event.select("div.eb-description-details > a").eachAttr("href").first()

            val startTimeText =
                event.select("tr:contains($beginString)").select("td.eb-event-property-value").eachText().first()
            val endTimeText =
                event.select("tr:contains($endString)").select("td.eb-event-property-value").eachText().first()

            val startTime = OffsetDateTime.of(LocalDateTime.from(formatter.parse(startTimeText)), offset)
            val endTime = OffsetDateTime.of(LocalDateTime.from(formatter.parse(endTimeText)), offset)

//            println()
//            println(title)
//            println()
//            println(description)
//            println(link)
//            println(startTimeText)
//            println(endTimeText)
//            println()
//            println("------------------------------")

            val additionalData = mutableMapOf<String, String>()

            additionalData[SemanticKeys.DESCRIPTION] = description
            additionalData[SemanticKeys.ENDDATE] = endTime.format(DateTimeFormatter.ISO_DATE_TIME)
            additionalData[SemanticKeys.LOCATION_URL] = "$baseUrl$link"

            events.add(createEvent(title, startTime, additionalData))

        }


        return events
    }

    override fun getName(): String = "Rheinbähnle Collector"
}