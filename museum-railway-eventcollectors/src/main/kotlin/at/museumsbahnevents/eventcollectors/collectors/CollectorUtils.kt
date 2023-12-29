package at.museumsbahnevents.eventcollectors.collectors

import java.time.Month
import java.time.format.DateTimeFormatterBuilder
import java.util.*

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

val yearRegex = Regex("202\\d")
val monthRegex =
    Regex("Jänner|Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember".lowercase())
val dateRegex = Regex("\\d{1,2}\\.")