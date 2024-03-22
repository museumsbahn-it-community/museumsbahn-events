import assertk.assertThat
import assertk.assertions.containsAtLeast
import assertk.assertions.isEqualTo
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import org.junit.jupiter.api.Test

class DateParserTest {
    @Test()
    fun `simple ÖGEG date should parse`() {
        val data = "23. MÄRZ 2024 \"RATSHERRNEXPRESS zum Freistädter Ostermarkt\""
        val dates = DateParser.parseAllDatesFrom(data)
        assertThat(dates.size).isEqualTo(1)
        assertThat(dates.first).isEqualTo(DateParser.createDate(2024, 3, 23))
    }


    @Test()
    fun `ÖGEG date with twice a year number in it should also parse`() {
        val data = "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE"
        val dates = DateParser.parseAllDatesFrom(data)
        assertThat(dates.size).isEqualTo(1) // a year alone is not a date
        assertThat(dates.first).isEqualTo(DateParser.createDate(2024, 1, 20))
    }

    @Test()
    fun `jänner and januar must both work`() {
        // with locale DE, java cannot parse Jänner, which is the usual form of writing in Austria
        val data1 = "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE"
        val data2 = "20. JANUAR 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE"
        val dates1 = DateParser.parseAllDatesFrom(data1)
        assertThat(dates1.size).isEqualTo(1)
        assertThat(dates1.first).isEqualTo(DateParser.createDate(2024, 1, 20))
        val dates2 = DateParser.parseAllDatesFrom(data2)
        assertThat(dates2.size).isEqualTo(1)
        assertThat(dates2.first).isEqualTo(DateParser.createDate(2024, 1, 20))
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
                DateParser.createDate(2023, 6, 4),
                DateParser.createDate(2023, 6, 11),
                DateParser.createDate(2023, 6, 18),
                DateParser.createDate(2023, 6, 25)
        )
    }

    @Test()
    fun `generic parsing of dates in strings should work`() {
        val testCases = mapOf(
                "Planzüge jeden Mittwoch, Samstag, Sonn- und Feiertag vom 17.07.2024 bis 28.08.2024" to 2,
                "an Sonntagen vom 2. Juli bis 1. Oktober 2023" to 2,
                "Donnerstag, 30. Mai bis Sonntag, 02. Juni" to 2,
                "Freitag und Samstag von 07.06. bis 29.06.2024" to 2,
                "Täglich vom 30.05. bis 02.06.2024" to 2,
                "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE" to 1, // a year alone is not a date
                "Samstag, 31. August + Sonntag, 01. September" to 2,
                "Samstag und Sonntag, 05. und 06. Oktober" to 2,
                "28.06.2024 ab 19:30 Uhr - KABARETT mit Günther Lainer & BOOGIE WOOGIE mit Axel Zwingenberger" to 1, // we don't care about time for now, date is painful enough
                "Sonntag, 7., 14., 21., und 28. Juli 2024" to 4,
        )

        testCases.forEach { (text, expectedDateCount) ->
            assertThat(DateParser.parseAllDatesFrom(text).size,
                    name = "$text should contain $expectedDateCount dates"
            ).isEqualTo(expectedDateCount)
        }
    }

    @Test()
    fun `parse duration test`() {
        // TODO: implement duration parsing
    }
}