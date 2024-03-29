package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.CATEGORY_MUSEUM_TRAIN
import at.museumrailwayevents.model.conventions.RecurrenceType
import at.museumrailwayevents.model.conventions.Registration
import at.museumrailwayevents.model.conventions.TAGS_NARROW_GAUGE
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import org.jsoup.Jsoup
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RheinbähnleCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "rheinschauen",
    locationId = "rheinschauen",
    locationName = "Rhein-Schauen | Museum und Rheinbähnle",
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
            try {
                val eventDetailsPath = event.select("a:contains(details)").attr("href")
                val eventDetailsUrl = baseUrl + eventDetailsPath
                val eventDetails = Jsoup.connect(eventDetailsUrl).get()

                val title = eventDetails.select("h1.eb-page-heading").eachText().first()
                val description = try {
                    eventDetails.select("div.eb-description-details > p").eachText().first()
                } catch (ex: Exception) {
                    println("error parsing description for: $title, reason: ${ex.message}")
                    ""
                }
                val link = eventDetails.select("div.eb-description-details > a").eachAttr("href").first()

                val startTimeText =
                    eventDetails.select("tr:contains($beginString)").select("td.eb-event-property-value").eachText()
                        .first()
                val endTimeText =
                    eventDetails.select("tr:contains($endString)").select("td.eb-event-property-value").eachText()
                        .first()

                val startTime = OffsetDateTime.of(LocalDateTime.from(formatter.parse(startTimeText)), offset)
                val endTime = OffsetDateTime.of(LocalDateTime.from(formatter.parse(endTimeText)), offset)
                val additionalData: MutableMap<String, String> = mutableMapOf(
                    SemanticKeys.DESCRIPTION to description,
                    SemanticKeys.ENDDATE to endTime.format(DateTimeFormatter.ISO_DATE_TIME),
                    SemanticKeys.TAGS to TAGS_NARROW_GAUGE.toTagsValue(),
                    SemanticKeys.REGISTRATION to Registration.PRE_SALES_ONLY,
                    SemanticKeys.CATEGORY to CATEGORY_MUSEUM_TRAIN,
                    SemanticKeys.LOCATION_URL to "$baseUrl$link",
                    SemanticKeys.ADDITIONAL_EVENTS_URL to url,
                    SemanticKeys.RECURRENCE_TYPE to RecurrenceType.RARELY,
                )
                events.add(createEvent(title, startTime, additionalData))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return events
    }

    override fun getName(): String = "Rheinbähnle Collector"
}