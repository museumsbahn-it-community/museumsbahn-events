package at.museumrailwayevents.eventcollectors.collectors.dateParser

import java.lang.Integer.parseInt
import java.time.Instant
import java.time.LocalTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoUnit
import java.util.*

object DateParser {

    val locale = Locale.GERMAN
    val zoneOffset = ZoneId.of("Europe/Vienna").rules.getOffset(Instant.now())

    val dateStartRegex = "(^|\\s|,)"
    val dateRegexString =
        "$dateStartRegex([0123])?[1-9](\\.|0\\.|\\s|$)" // if we would make the '.' mandatory I am sure it would break eventually
    val dateRegexStringPartial = "([0123])?\\d{1}\\.?"
    val numericDayRegex = Regex(dateRegexString)
    val monthOptions = "Jänner|Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember"
    val monthOptionsLowercase = monthOptions.lowercase()
    val monthWrittenRegexString =
        "$dateStartRegex($monthOptions|$monthOptionsLowercase)(,|\\s|$)"
    val monthWrittenRegex = Regex(monthWrittenRegexString)

    // numeric month has a . at the end almost always
    // we have not yet seen a case where the month is numeric and there is no dot at the end
    // also month usually does not appear alone, hence we put the date in front of it
    // the match then has to be split
    val monthNumericRegexString = "($dateRegexStringPartial)(\\s+|^|\\.)(10|11|12|(0)?[1-9])\\."
    val monthNumericRegex = Regex(monthNumericRegexString)
    val monthRegex = Regex("($monthNumericRegexString)|(${monthWrittenRegexString})")
    val monthRegexStringPartial = "($monthNumericRegexString)|(${monthWrittenRegexString})"
    val yearRegexString = "202\\d"
    val yearRegex = Regex(yearRegexString)

    val nonAlphanumericAtStartRegex = Regex("^[^A-Za-z0-9]+")
    val nonAlphanumericAtEndRegex = Regex("[^A-Za-z0-9]+$")

    val durationRecurrenceRegexString =
        "Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag|Sonn- und Feiertag|täglich|jeden Tag".lowercase()

    val fullDateRegexString = "($dateRegexStringPartial)\\s*($monthRegexStringPartial)\\s*($yearRegexString)?"
    val fullDateRegex = Regex(fullDateRegexString)

    val durationStringRegex = Regex("(${fullDateRegexString}|von|vom).*(bis)")

    val timeRegexString = "((0?[0-9])|([1-2][0-9])):[0-5][0-9]"
    val timeRegex = Regex(timeRegexString)

    fun parseDate(text: String): OffsetDateTime {
        val lowercaseText = text.lowercase()
        val year = findFirstYearOrAssumeDefault(lowercaseText)
        val month = findSingleMonth(lowercaseText)
        val day = findAllDays(lowercaseText).first()

        try {
            return createDate(year, month, day)
        } catch (ex: Exception) {
            throw Exception("error parsing $text", ex)
        }
    }

    fun tryParseDate(text: String): OffsetDateTime? {
        return try {
            parseDate(text)
        } catch (ex: Exception) {
            null
        }
    }

    fun parseIsoDate(date: String, time: String): OffsetDateTime {
        return OffsetDateTime.parse("${date}T$time", DateTimeFormatter.ISO_DATE_TIME.withZone(zoneOffset))
    }

    fun MatchResult.toDayValue(): Int = value.trim('.').trim().toInt()

    fun findSingleYear(text: String): Int {
        val years = yearRegex.findAll(text)
        if (years.count() != 1) {
            throw RuntimeException("expected single year in text: $text")
        }
        return years.first().value.trim().toInt()
    }

    /**
     * Try to find a year in the given string or default to the current year.
     * Not 100% correct, but good enough.
     */
    fun findFirstYearOrAssumeDefault(text: String): Int {
        val years = yearRegex.findAll(text)
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
        val monthNumber = match.value.trim().split(".")[1].trim()
        return Month.of(parseInt(monthNumber))
    }

    fun findSingleMonth(text: String): Month {
        val numericMonths = monthNumericRegex.findAll(text.lowercase())
        val writtenMonths = monthWrittenRegex.findAll(text.lowercase())
        if (numericMonths.count() != 1 && writtenMonths.count() != 1) {
            throw Exception("there should be either one written month or one numeric month: $text")
        }
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
        }.trimNonAlphanumeric()

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
            val year = findFirstYearOrAssumeDefault(lowercaseText)
            val month = findSingleMonth(lowercaseText)
            return days.map {
                createDate(year, month, it)
            }
        }
    }

    fun removeDateAndTimeFrom(text: String): String {
        return text
            .replace(fullDateRegex, "")
            .replace(numericDayRegex, "")
            .replace(timeRegex, "")
            .trimNonAlphanumeric()
    }

    fun String.trimNonAlphanumeric() =
        this.replace(nonAlphanumericAtStartRegex, "")
            .replace(nonAlphanumericAtEndRegex, "")


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

    fun generateDateListBetween(startDate: OffsetDateTime, endDate: OffsetDateTime): List<OffsetDateTime> {
        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)
        val dates = mutableListOf(startDate, endDate)
        var currDate = startDate.plusDays(1)
        repeat(daysBetween.toInt()) {
            dates.add(currDate)
            currDate = currDate.plusDays(1)
        }
        return dates
    }

    fun findSingleYearOrNull(text: String): Int? {
        return try {
            findSingleYear(text)
        } catch (ex: Exception) {
            null
        }
    }

    fun parseAllTimesFrom(text: String): List<OffsetTime> {
        val lowercaseText = text.lowercase()
        val times = timeRegex.findAll(lowercaseText).map { it.value }.toList()

        return times.map { match ->
            parseTime(match)
        }.toList()
    }

    private fun parseTime(match: String): OffsetTime {
        return LocalTime.parse(match).atOffset(zoneOffset)
    }
}