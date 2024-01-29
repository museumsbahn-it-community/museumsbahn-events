package at.museumrailwayevents.eventcollectors.collectors

import base.boudicca.model.Event
import org.jsoup.Jsoup
import java.time.*
import java.util.*

class OegegSchmalspurCollector : MuseumRailwayEventCollector(
    operatorId = "oegeg",
    locationId = "oegeg_schmalspur",
    url = "https://www.oegeg.at/termine/termine-schmalspur-steyrtalbahn/"
) {
    private val eventTitle = "Steyrtalbahn Fahrt"

    override fun collectEvents(): List<Event> {
        val document = Jsoup.connect(url).get()
        val eventBoxes = document.select("div.cc-m-textwithimage-inline-rte")

        // dates on this website can be split having multiple dates in the same line
        // eg. Sonntag, 4., 11., 18. und 25. Juni 2023
        val events = mutableListOf<Event>()

        val zuglokKeyword = "Zuglok"
        eventBoxes.forEach { eventBox ->
            val dateString = eventBox.select("span").eachText().firstOrNull {
                dateRegex.containsMatchIn(it) && monthRegex.containsMatchIn(it.lowercase()) && !it.contains(
                    zuglokKeyword
                )
            }
            val zuglokString = eventBox.select("span").eachText().firstOrNull {
                it.contains(zuglokKeyword)
            }

            if (dateString == null) {
                // some of the boxes are just used for additional info
                // if there are no dates, then skip it
                return@forEach
            }

            val years = yearRegex.findAll(dateString)
            assert(years.count() == 0 || years.count() == 1)
            val year = if (years.count() == 0) {
                OffsetDateTime.now().year
                // if no year is given, then default to the current year
                // this implementation might cause trouble if executed on 31st Dec or 1st Jan, but otherwise should work fine
            } else {
                years.first().value.trim().toInt()
            }

            val months = monthRegex.findAll(dateString.lowercase())
            assert(months.count() == 1)
            val monthName = months.first().value.trim()
            val month = convertMonthNameToMonth(monthName, Locale("de", "DE"))

            val dates = dateRegex.findAll(dateString).toList()

            val offset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())
            dates.forEach { dateMatch ->
                val dateValue = dateMatch.mapToDateValue()
                val startDate =
                    OffsetDateTime.of(
                        year,
                        month.value,
                        dateValue,
                        0, 0, 0, 0,
                        offset
                    )
                val additionalData = mutableMapOf<String, String>()

                val parsedZuglok = parseZuglok(dateValue, zuglokString)
                if (parsedZuglok != null) {
                    additionalData["lokomotive"] = parsedZuglok
                }

                events.add(createEvent(eventTitle, startDate, additionalData))
            }
        }

        return events
    }


    private fun parseZuglok(dateValueOfEvent: Int, zuglokString: String?): String? {
        if (zuglokString == null) {
            return null
        }

        val date = dateRegex.findAll(zuglokString).firstOrNull()?.mapToDateValue() ?: return null
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

private fun MatchResult.mapToDateValue(): Int = value.trim('.').toInt()