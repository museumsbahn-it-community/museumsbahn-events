package at.museumrailwayevents.eventcollectors.collectors.dateParser

import java.time.Instant
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.util.*

object DateParser {

    val locale = Locale.GERMAN
    val zoneOffset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())

    val yearRegexString = "202\\d"
    val yearRegex = Regex(yearRegexString)
    val monthWrittenRegexString =
        "Jänner|Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember".lowercase()
    val monthWrittenRegex = Regex(monthWrittenRegexString)
    val monthRegexString = "(10|11|12|[1-9])\\."

    val dateRegexString = "([123])?\\d{1}\\."
    val dateRegex = Regex(dateRegexString)

    val fullDateRegex = "$dateRegexString$monthWrittenRegexString$yearRegexString|"

    fun parseDate(data: String) {

    }

    fun findSingleYear(text: String): Int {
        val years = yearRegex.findAll(text)
        assert(years.count() == 1)
        return years.first().value.trim().toInt()
    }

    /**
     * Try to find a year in the given string or default to the current year.
     * Not 100% correct, but good enough.
     */
    fun findSingleYearOrAssumeDefault(text: String): Int {
        val years = yearRegex.findAll(text)
        assert(years.count() == 0 || years.count() == 1)
        return if (years.count() == 0) {
            OffsetDateTime.now().year
            // if no year is given, then default to the current year
            // this implementation might cause trouble if executed on 31st Dec or 1st Jan, but otherwise should work fine
        } else {
            years.first().value.trim().toInt()
        }
    }

    fun findSingleWrittenMonth(text: String): Month {
        val writtenMonths = monthWrittenRegex.findAll(text.lowercase())
        assert(writtenMonths.count() == 1)
        val monthName = writtenMonths.first().value.trim()
        return convertMonthNameToMonth(monthName, locale)
    }

    fun findAllDayDates(text: String): List<String> {
        val dates = dateRegex.findAll(text)
        return dates.map { it.value }.toList()
    }

    /**
     * Converts the string of a given month to a parseable month.
     */
    fun convertMonthNameToMonth(monthName: String, locale: Locale): Month {
        val actualMonthName = if (monthName.lowercase().equals("jänner")) {
            // jvm is stupid and doesn't how to properly spell months!
            "Januar";
        } else {
            monthName
        }

        val builder = DateTimeFormatterBuilder()
        builder.parseCaseInsensitive()
        builder.appendPattern("MMMM")
        val formatter = builder.toFormatter(locale)
        return Month.from(formatter.parse(actualMonthName))
    }


}