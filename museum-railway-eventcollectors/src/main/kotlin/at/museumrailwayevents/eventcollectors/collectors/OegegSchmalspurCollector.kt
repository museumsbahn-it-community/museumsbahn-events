package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.createDate
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.findAllDays
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.numericDayRegex
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.monthWrittenRegex
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.toDayValue
import at.museumrailwayevents.eventcollectors.service.JsoupCrawler
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import org.jsoup.Jsoup

class OegegSchmalspurCollector(val jsoupCrawler: JsoupCrawler) : MuseumRailwayEventCollector(
    operatorId = "oegeg",
    locationId = "oegeg_schmalspur",
    locationName = "ÖGEG Steyrtalbahn",
    url = "https://www.oegeg.at/termine/termine-schmalspur-steyrtalbahn/"
) {
    private val eventTitle = "Steyrtal Museumsbahn"

    override fun collectEvents(): List<Event> {
        val document = jsoupCrawler.getDocument(url)
        val eventBoxes = document.select("div.j-textWithImage")

        // dates on this website can be split having multiple dates in the same line
        // eg. Sonntag, 4., 11., 18. und 25. Juni 2023
        val events = mutableListOf<Event>()

        val zuglokKeyword = "Zuglok"
        eventBoxes.forEach { eventBox ->
            val dateString = eventBox.select("span").eachText().firstOrNull {
                numericDayRegex.containsMatchIn(it) && monthWrittenRegex.containsMatchIn(it.lowercase()) && !it.contains(
                    zuglokKeyword
                )
            }
            val zuglokString = eventBox.select("span").eachText().firstOrNull {
                it.contains(zuglokKeyword)
            }
            val imageElement = eventBox.select("img").firstOrNull();
            val pictureUrl = imageElement?.absUrl("src");
            val description = eventBox.select("p").eachText().joinToString("\n")

            if (dateString == null) {
                // some of the boxes are just used for additional info
                // if there are no dates, then skip it
                return@forEach
            }
            println(dateString)

            val year = DateParser.findFirstYearOrAssumeDefault(dateString)
            val month = DateParser.findSingleWrittenMonth(dateString)
            val dates = findAllDays(dateString)

            println(dates)

            dates.forEach { dateValue ->
                val startDate =
                        createDate(year, month, dateValue)
                val additionalData = mutableMapOf<String, String>()

                val parsedZuglok = parseZuglok(dateValue, zuglokString)
                if (parsedZuglok != null) {
                    additionalData["lokomotive"] = parsedZuglok
                }
                if (pictureUrl != null) {
                    additionalData[SemanticKeys.PICTURE_URL] = pictureUrl
                }
                additionalData[SemanticKeys.DESCRIPTION] = description

                println("create event: ${eventTitle} - ${startDate}")
                events.add(createEvent(eventTitle, startDate, additionalData))
            }
        }

        return events
    }

    private fun parseZuglok(dateValueOfEvent: Int, zuglokString: String?): String? {
        if (zuglokString == null) {
            return null
        }

        val date = numericDayRegex.findAll(zuglokString).firstOrNull()?.toDayValue() ?: return null
        if (date == dateValueOfEvent) {
            val trimmedString = zuglokString.split(":").last().trim()
            if (trimmedString.isBlank()) {
                return null
            } else {
                return trimmedString
            }
        }
        return null
    }

    override fun getName(): String = "ÖGEG Schmalspur Termine"
}