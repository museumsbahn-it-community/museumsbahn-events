package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model

import java.time.DayOfWeek
import java.time.LocalDate

class DateProgression(
    override val start: LocalDate, override val endInclusive: LocalDate, private val stepDays: List<DayOfWeek> = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY,
    )
) : Iterable<LocalDate>, ClosedRange<LocalDate> {

    override fun iterator(): Iterator<LocalDate> = DateIterator(start, endInclusive, stepDays)

    infix fun days(days: List<DayOfWeek>) = DateProgression(start, endInclusive, days)
}

operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)