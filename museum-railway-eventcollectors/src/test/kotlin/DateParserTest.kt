import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsAtLeast
import assertk.assertions.isEqualTo
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser.createDate
import org.junit.jupiter.api.Test

class DateParserTest {
    @Test()
    fun `simple ÖGEG date should parse`() {
        val data = "23. MÄRZ 2024 \"RATSHERRNEXPRESS zum Freistädter Ostermarkt\""
        val dates = DateParser.parseAllDatesFrom(data)
        assertThat(dates.size).isEqualTo(1)
        assertThat(dates.first).isEqualTo(createDate(2024, 3, 23))
    }


    @Test()
    fun `ÖGEG date with twice a year number in it should also parse`() {
        val data = "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE"
        val dates = DateParser.parseAllDatesFrom(data)
        assertThat(dates.size).isEqualTo(1) // a year alone is not a date
        assertThat(dates.first).isEqualTo(createDate(2024, 1, 20))
    }

    @Test()
    fun `jänner and januar must both work`() {
        // with locale DE, java cannot parse Jänner, which is the usual form of writing in Austria
        val data1 = "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE"
        val data2 = "20. JANUAR 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE"
        val dates1 = DateParser.parseAllDatesFrom(data1)
        assertThat(dates1.size).isEqualTo(1)
        assertThat(dates1.first).isEqualTo(createDate(2024, 1, 20))
        val dates2 = DateParser.parseAllDatesFrom(data2)
        assertThat(dates2.size).isEqualTo(1)
        assertThat(dates2.first).isEqualTo(createDate(2024, 1, 20))
    }

    @Test()
    fun `parsing multi day event`() {
        val data = "28.-30. JUNI 2024 \"50 JAHRE-ÖGEG\" Sonderveranstaltung mit Jubiläumsprogramm - 1. SPUR 1-MESSE"
        val dates = DateParser.parseAllDatesFrom(data)
        assertThat(dates.size).isEqualTo(2)
        // implementation of date ranges is not yet correct, but at least the two dates should be recognized
    }

    @Test()
    fun `multiple dates in same string`() {
        val data = "Sonntag, 4., 11., 18. und 25. Juni 2023"
        val dates = DateParser.parseAllDatesFrom(data)
        assertThat(dates.size).isEqualTo(4)
        assertThat(dates).containsAtLeast(
            createDate(2023, 6, 4),
            createDate(2023, 6, 11),
            createDate(2023, 6, 18),
            createDate(2023, 6, 25)
        )
    }

    @Test()
    fun `generic parsing of single dates in strings should work`() {
        val testCases = mapOf(
            "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE" to
                    createDate(2024, 1, 20), // a year alone is not a date
            "28.06.2024 ab 19:30 Uhr - KABARETT mit Günther Lainer & BOOGIE WOOGIE mit Axel Zwingenberger" to
                    createDate(2024, 6, 28), // we don't care about time for now, date is painful enough
            "gültig am 11.05.2024" to createDate(2024, 5, 11),
        )

        testCases.forEach { (text, expectedDate) ->
            val parsedDate = DateParser.parseDate(text)
            assertThat(
                parsedDate,
                name = "$text should be parsed as date $expectedDate"
            ).isEqualTo(expectedDate)
        }
    }

    @Test()
    fun `generic parsing of multiple dates in strings should work`() {
        val testCases = mapOf(
            "Planzüge jeden Mittwoch, Samstag, Sonn- und Feiertag vom 17.07.2024 bis 28.08.2024" to listOf(
                createDate(2024, 7, 17),
                createDate(2024, 8, 28)
            ),
            "an Sonntagen vom 2. Juli bis 1. Oktober 2023" to listOf(
                createDate(2024, 7, 2), // TODO: this is a bug that needs fixing
                createDate(2023, 10, 1)
            ),
            "Donnerstag, 30. Mai bis Sonntag, 02. Juni" to listOf(
                createDate(2024, 5, 30),
                createDate(2024, 6, 2)
            ),
            "Freitag und Samstag von 07.06. bis 29.06.2024" to listOf(
                createDate(2024, 6, 7),
                createDate(2024, 6, 29)
            ),
            "Täglich vom 30.05. bis 02.06.2024" to listOf(
                createDate(2024, 5, 30),
                createDate(2024, 6, 2)
            ),
            "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE" to listOf(
                createDate(2024, 1, 20),
            ), // a year alone is not a date
            "Samstag, 31. August + Sonntag, 01. September" to listOf(
                createDate(2024, 8, 31),
                createDate(2024, 9, 1),
            ),
            "Samstag und Sonntag, 05. und 06. Oktober" to listOf(
                createDate(2024, 10, 5),
                createDate(2024, 10, 6),
            ),
            "28.06.2024 ab 19:30 Uhr - KABARETT mit Günther Lainer & BOOGIE WOOGIE mit Axel Zwingenberger" to listOf(
                createDate(2024, 6, 28),
            ), // we don't care about time for now, date is painful enough
            "Sonntag, 7., 14., 21., und 28. Juli 2024" to listOf(
                createDate(2024, 7, 7),
                createDate(2024, 7, 14),
                createDate(2024, 7, 21),
                createDate(2024, 7, 28),
            ),
            " am 1., 8.,15. und 22. Juni 2024" to listOf(
                createDate(2024, 6, 1),
                createDate(2024, 6, 8),
                createDate(2024, 6, 15),
                createDate(2024, 6, 22),
            ),
            "gültig am 11.05.2024" to listOf(
                createDate(2024, 5, 11),
            ),
        )

        testCases.forEach { (text, expectedDates) ->
            val parsedDates = DateParser.parseAllDatesFrom(text)
            assertThat(
                parsedDates.size,
                name = "$text should contain $expectedDates dates"
            ).isEqualTo(expectedDates.size)

            expectedDates.forEach {
                assertThat(parsedDates).contains(it)
            }
        }
    }

    @Test()
    fun `parse duration test`() {
        // TODO: implement duration parsing
    }
}