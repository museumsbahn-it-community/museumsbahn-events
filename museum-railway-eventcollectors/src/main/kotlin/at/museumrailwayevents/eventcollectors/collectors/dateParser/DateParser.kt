package at.museumrailwayevents.eventcollectors.collectors.dateParser

import java.lang.Integer.parseInt
import java.time.Instant
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.util.*

object DateParser {

    val locale = Locale.GERMAN
    val zoneOffset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())

    val dateRegexString = "(^|\\s)([0123])?[1-9](\\.|0\\.|\\s|$)" // if we would make the '.' mandatory I am sure it would break eventually
    val dateRegexStringPartial = "([0123])?\\d{1}\\.?"
    val numericDayRegex = Regex(dateRegexString)
    val monthWrittenRegexString =
            "Jänner|Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember".lowercase()
    val monthWrittenRegex = Regex(monthWrittenRegexString)

    // numeric month has a . at the end almost always
    // we have not yet seen a case where the month is numeric and there is no dot at the end
    // also month usually does not appear alone, hence we put the date in front of it
    // the match then has to be split
    val monthNumericRegexString = "($dateRegexStringPartial)?(\\s+|^|\\.)(10|11|12|(0)?[1-9])\\."
    val monthNumericRegex = Regex(monthNumericRegexString)
    val monthRegex = Regex("($monthNumericRegexString)|(${monthWrittenRegexString})")
    val monthRegexStringPartial = "($monthNumericRegexString)|(${monthWrittenRegexString})"
    val yearRegexString = "202\\d"
    val yearRegex = Regex(yearRegexString)

    val durationRecurrenceRegexString = "Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag|Sonn- und Feiertag|täglich|jeden Tag".lowercase()

    val fullDateRegexString = "($dateRegexStringPartial)\\s*($monthRegexStringPartial)\\s*($yearRegexString)?"
    val fullDateRegex = Regex(fullDateRegexString)

    val durationStringRegex = Regex("(${fullDateRegexString}|von|vom).*(bis)")

    fun parseDate(text: String): OffsetDateTime {
        val lowercaseText = text.lowercase()
        val year = findSingleYearOrAssumeDefault(lowercaseText)
        val month = findSingleMonth(lowercaseText)
        val day = findAllDays(lowercaseText).first()

        try {
            return createDate(year, month, day)
        } catch (ex: Exception) {
            throw Exception("error parsing $text", ex)
        }
    }

    fun MatchResult.toDayValue(): Int = value.trim('.').toInt()

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
        return parseWrittenMonthFromMatch(writtenMonths.first())
    }

    private fun parseWrittenMonthFromMatch(match: MatchResult): Month {
        val monthName = match.value.trim()
        return convertMonthNameToMonth(monthName, locale)
    }

    fun findSingleNumericMonth(text: String): Month {
        val numericMonths = monthNumericRegex.findAll(text.lowercase())
        assert(numericMonths.count() == 1)
        // the regex searches for date/month combined occurence like
        // 01.05., 26.10. and so on. so we have to split it
        return parseNumericMonthFromMatch(numericMonths.first())
    }

    private fun parseNumericMonthFromMatch(match: MatchResult): Month {
        val monthNumber = match.value.trim().split(".")[1]
        return Month.of(parseInt(monthNumber))
    }

    fun findSingleMonth(text: String): Month {
        val numericMonths = monthNumericRegex.findAll(text.lowercase())
        val writtenMonths = monthWrittenRegex.findAll(text.lowercase())
        assert(numericMonths.count() == 1 || writtenMonths.count() == 1) { "there should be either one written month or one numeric month: $text" }
        return if (writtenMonths.count() == 1) {
            parseWrittenMonthFromMatch(writtenMonths.first())
        } else {
            parseNumericMonthFromMatch(numericMonths.first())
        }
    }

    fun findAllDays(text: String): List<Int> {
        val dates = numericDayRegex.findAll(text).map { match -> match.value.filter { it.isDigit() } }
        return dates.map { parseInt(it) }.toList()
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

    fun isDurationString(text: String): Boolean {
        return text.matches(durationStringRegex)
    }

    fun getFromDateForDuration(text: String): OffsetDateTime {
        val date = numericDayRegex.findAll(text).first().value
        return parseDate(date)
    }

    fun getToDateForDuration(text: String): OffsetDateTime {
        // TODO: pretty certain this will crash if the string is malformed
        val date = numericDayRegex.findAll(text).toList()[1].value
        return parseDate(date)
    }

    fun getWeekdaysForDuration(text: String): List<String> {


        return emptyList()
    }

    fun parseAllDatesFrom(text: String): List<OffsetDateTime> {
        val lowercaseText = text.lowercase()
        val fullDates = fullDateRegex.findAll(lowercaseText).map { it.value }.toList()
        val days = findAllDays(lowercaseText)

        if (days.size == fullDates.size) {
            return fullDates.map { match ->
                parseDate(match)
            }.toList()
        } else {
            val year = findSingleYearOrAssumeDefault(lowercaseText)
            val month = findSingleMonth(lowercaseText)
            return days.map {
                createDate(year, month, it)
            }
        }
    }

    fun createDate(year: Int, month: Int, day: Int): OffsetDateTime =
            OffsetDateTime.of(
                    year,
                    month,
                    day,
                    0, 0, 0, 0,
                    zoneOffset,
            )

    fun createDate(year: Int, month: Month, day: Int): OffsetDateTime =
            createDate(year, month.value, day)
}