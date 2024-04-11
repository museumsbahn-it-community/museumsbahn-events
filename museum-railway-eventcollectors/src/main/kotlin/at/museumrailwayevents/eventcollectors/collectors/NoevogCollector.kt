package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.fullDateRegexString
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import at.museumrailwayevents.model.conventions.CATEGORY_MUSEUM_TRAIN
import at.museumrailwayevents.model.conventions.CommonKeys
import at.museumrailwayevents.model.conventions.RecurrenceType
import at.museumrailwayevents.model.conventions.Registration
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import java.time.OffsetDateTime

abstract class NoevogCollector(
    val jsoupCrawler: JsoupCrawler,
    operatorId: String,
    locationId: String,
    url: String,
    tags: List<String> = emptyList(),
    locationName: String
) : MuseumRailwayEventCollector(
    operatorId, locationId, url, tags, locationName
) {
    private fun isNoevogDurationString(it: String): Boolean {
        val regex = Regex("von$fullDateRegexString(\\s)*-$fullDateRegexString")
        return it.matches(regex)
    }

    protected fun collectSonderfahrten(
        sonderfahrtenUrl: String,
        tags: String,
        locomotiveType: String,
        excludeByTitle: List<Regex> = emptyList(),
        excludeByBody: List<Regex> = emptyList(),
        removeFromTitle: List<Regex> = emptyList(),
    ): List<Event> {
        val events = mutableListOf<Event>()
        val document = jsoupCrawler.getDocument(sonderfahrtenUrl)

        val links = document.select("article.offer > h3 > a").eachAttr("href")
        links.forEach linksLoop@{ eventUrl ->
            val eventDocument = jsoupCrawler.getDocument(eventUrl).select("section.content")
            var title = eventDocument.select("h1").eachText().first().trim()
            val description = eventDocument.select("article.bodycopy > div > p").eachText().joinToString("\n")
            val dateStrings = eventDocument.select("h3:contains(Angebotszeitraum) ~ ul > li").eachText()

            excludeByTitle.forEach { regex ->
                if (title.contains(regex)) {
                    return@linksLoop
                }
            }

            excludeByBody.forEach { regex ->
                if (description.contains(regex)) {
                    return@linksLoop
                }
            }

            if (dateStrings.size == 0) {
                println("no 'Angebotszeitraum' for $title found")
                return@linksLoop
            }

            removeFromTitle.forEach {
                title = title.replace(it, "").trim()
            }

            val dates = dateStrings.flatMap {
                val dates = DateParser.parseAllDatesFrom(it)
                if (isNoevogDurationString(it)) {
                    return@flatMap DateParser.generateDateListBetween(dates[0], dates[1])
                }
                return@flatMap dates
            }

            val recurrence = when (dates.size) {
                1, 2 -> RecurrenceType.ONCE
                in 2..5 -> RecurrenceType.RARELY
                else -> RecurrenceType.REGULARLY
            }

            dates.forEach { date ->
                events.add(
                    createNoevogEvent(
                        title,
                        date,
                        eventUrl,
                        description,
                        recurrence,
                        locomotiveType,
                        tags
                    )
                )
            }
        }

        return events
    }

    private fun createNoevogEvent(
        name: String,
        date: OffsetDateTime,
        url: String,
        description: String,
        recurrenceType: String,
        locomotiveType: String,
        tags: String,
    ) = createEvent(
        name, date, additionalData = mutableMapOf(
            SemanticKeys.CATEGORY to CATEGORY_MUSEUM_TRAIN,
            SemanticKeys.URL to url,
            SemanticKeys.RECURRENCE_TYPE to recurrenceType,
            SemanticKeys.REGISTRATION to Registration.TICKET,
            SemanticKeys.DESCRIPTION to description,
            CommonKeys.LOCOMOTIVE_TYPE to locomotiveType,
            SemanticKeys.TAGS to tags,
        )
    )
}