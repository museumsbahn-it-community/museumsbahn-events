package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model

import java.time.DayOfWeek
import java.time.LocalDate

class DateIterator(
    val startDate: LocalDate,
    val endDateInclusive: LocalDate,
    val stepDays: List<DayOfWeek>
) : Iterator<LocalDate> {
    private val lastDay: LocalDate
    private var currentDate = startDate

    init {
        if (stepDays.isEmpty()) {
            throw Exception("at least one day of week must be given")
        }

        while (!stepDays.contains(currentDate.dayOfWeek) && currentDate < endDateInclusive) {
            currentDate = currentDate.plusDays(1)
        }

        var lastDay = endDateInclusive
        while (!stepDays.contains(lastDay.dayOfWeek)) {
            lastDay = lastDay.minusDays(1)
        }
        this.lastDay = lastDay
    }

    override fun hasNext() = currentDate <= lastDay

    override fun next(): LocalDate {

        val next = currentDate

        do {
            currentDate = currentDate.plusDays(1)
        } while (!stepDays.contains(currentDate.dayOfWeek))

        return next
    }

}