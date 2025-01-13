package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl

import at.museumrailwayevents.eventcollectors.collectors.MuseumRailwayEventCollector
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model.RecurringInformationFixedDates
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model.RecurringInformationWeekdays
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model.SternUndHafferlEvent
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model.sternUndHafferlJsonModule
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model.rangeTo
import at.museumrailwayevents.eventcollectors.collectors.util.keepLineBreaks
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.lang.Integer.parseInt
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.OffsetDateTime

abstract class SternHafferlCollector(
    val jsoupCrawler: JsoupCrawler,
    val locomotiveType: String,
    operatorId: String,
    locationId: String,
    sourceUrl: String,
    tags: List<String> = emptyList(),
    locationName: String,
    val eventJsonApiUrl: String,
    // the api is mixed for traunseetram and atterseebahn
    val filterIncludeStrings: List<String> = emptyList(),
    val filterExcludeStrings: List<String> = emptyList()
) : MuseumRailwayEventCollector(
    operatorId, locationId, sourceUrl, tags, locationName
) {

    protected fun collectEventsFromPage(url: String): List<Event> {
        val rawEvents = getEvents(eventJsonApiUrl)
        val events = mutableListOf<Event>()
        rawEvents.filter {
            it.startdate != null && it.fromTime != null
        }.forEach { event ->
            if (event.recurring_event) {
                val dates = mutableListOf<LocalDateTime>()
                event.recurring_information?.forEach { info ->
                    if (info is RecurringInformationWeekdays) {
                        val weekdays = info.weekday.days.map {
                            val weekdayValue = parseInt(it)
                            DayOfWeek.of(weekdayValue)
                        }
                        val startDate = event.startdate
                        val endDate = event.enddate

                        if (startDate == null || endDate == null) {
                            println("[SternUndHafferl] startdate and enddate required for recurring event, but not set: ${event.parsedTitle}")
                            return@forEach
                        }

                        println("[SternUndHafferl] event dates for event: ${event.parsedTitle}")
                        try {
                            for (date in startDate..endDate days weekdays) {
                                val eventTime = date.atTime(event.fromTime)
                                println(eventTime)
                                dates.add(eventTime)
                            }

                        } catch (ex: Exception) {
                            println("[SternUndHafferl] error parsing dates for ${event.parsedTitle}")
                            println("weekdays: ${info.weekday.days}")
                            println(ex.message)
                        }
                    } else if (info is RecurringInformationFixedDates) {
                        info.fixedDates.dates.forEach { date ->
                            val time = event.fromTime
                            val dateTime = date.date.atTime(time)
                            dates.add(dateTime)
                        }
                    }
                }

                if (dates.isEmpty()) {
                    println("[SternUndHafferl] warning: recurring event, but no dates found: ${event.parsedTitle}")
                }

                dates.forEach {
                    events.add(
                        createSternUndHafferlEvent(event, it.atOffset(DateParser.zoneOffset), url)
                    )
                }

            } else {
                events.add(
                    createOneTimeEvent(event, url)
                )
            }
        }

        return events.filterEvents(filterIncludeStrings, filterExcludeStrings)
    }

    private fun createOneTimeEvent(
        event: SternUndHafferlEvent,
        url: String
    ): Event {
        val startDate = event.getStartDate()
        return createSternUndHafferlEvent(event, startDate, url)
    }

    private fun createSternUndHafferlEvent(
        event: SternUndHafferlEvent,
        startDate: OffsetDateTime,
        eventUrl: String
    ): Event {
        val description = getEnhancedDescription(event)

        return createEvent(
            event.parsedTitle,
            startDate,
            eventUrl,
            mutableMapOf(
                SemanticKeys.CATEGORY to Category.SPECIAL_TRIP,
                SemanticKeys.REGISTRATION to Registration.TICKET,
                SemanticKeys.DESCRIPTION to description,
                CommonKeys.VEHICLE_TYPE to locomotiveType,
                SemanticKeys.RECURRENCE_TYPE to RecurrenceType.RARELY,
                SemanticKeys.TAGS to TAGS_MUSEUM_EVENT.toTagsValue(),
                SemanticKeys.PICTURE_URL to event.featured_image_url,
                SemanticKeys.PICTURE_COPYRIGHT to "© Stern & Hafferl",
                SemanticKeys.URL to event.url,
                SemanticKeys.ADDITIONAL_EVENTS_URL to eventUrl,
            ),
        )
    }

    private fun getEvents(eventDateUrl: String): List<SternUndHafferlEvent> {
        val eventData = mutableListOf<SternUndHafferlEvent>()
        val job = GlobalScope.launch {
            val httpClient = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        classDiscriminator = "type"
                        serializersModule = sternUndHafferlJsonModule
                    })
                }
                install(Logging) {
                    level = LogLevel.HEADERS
                    sanitizeHeader { header -> header == HttpHeaders.Authorization }
                }
            }

            val datesResponse =
                httpClient.request(eventDateUrl)

            val parsedEvents: List<SternUndHafferlEvent> = datesResponse.body()
            eventData.addAll(parsedEvents)
        }
        runBlocking {
            job.join()
        }
        return eventData
    }


    private fun getEnhancedDescription(event: SternUndHafferlEvent): String {
        val url = event.url

        if (url.isNotBlank()) {
            val document = jsoupCrawler.getDocument(url).keepLineBreaks(after = listOf("ul", "li"))
            val title = document.select("h1").eachText()
            val text = document.select("h1 ~ p,h1 ~ div").eachText()

            val description = "${title.joinToString("\n")}\n\n${text.joinToString("\n")}"
            return description
        }

        return ""
    }

}

private fun MutableList<Event>.filterEvents(
    filterIncludeStrings: List<String>,
    filterExcludeStrings: List<String>
): List<Event> {
    return this.filter { event ->
        val textToFilter = "${event.name} ${event.data.get(SemanticKeys.DESCRIPTION)}"
        val containsAnyIncludeFilter =
            filterIncludeStrings.map { textToFilter.lowercase().contains(it.lowercase()) }.any { it }
        val containsAnyExcludeFilter =
            filterExcludeStrings.map { textToFilter.lowercase().contains(it.lowercase()) }.any { it }

        (filterIncludeStrings.isEmpty() || containsAnyIncludeFilter) && !containsAnyExcludeFilter
    }
}

private fun SternUndHafferlEvent.getStartDate(): OffsetDateTime =
    OffsetDateTime.of(this.startdate, this.fromTime, DateParser.zoneOffset)
