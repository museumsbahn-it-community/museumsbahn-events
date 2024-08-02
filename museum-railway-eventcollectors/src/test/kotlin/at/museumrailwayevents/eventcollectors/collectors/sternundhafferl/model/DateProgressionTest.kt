package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model


import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate

class DateProgressionTest {

    @Test
    fun `date progression should give the correct days`() {
        val startDate = LocalDate.of(2024, 7, 1)
        val endDate = LocalDate.of(2024, 7, 31)

        for (date in startDate..endDate days listOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)) {
            println("${date.dayOfWeek} $date ")
        }
    }


}