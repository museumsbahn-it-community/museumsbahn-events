import org.junit.jupiter.api.Test

class DateParserTest {
    @Test()
    fun `simple ÖGEG date should parse`() {
        val data = "23. MÄRZ 2024 \"RATSHERRNEXPRESS zum Freistädter Ostermarkt\""


    }


    @Test()
    fun `ÖGEG date with twice a year number in it should also parse`() {
        val data = "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE"


    }

    @Test()
    fun `jänner and januar must both work`() {
        // with locale DE, java cannot parse Jänner, which is the usual form of writing in Austria
        val data = "20. JÄNNER 2024 ZUR ERÖFFNUNG DER \"KULTURHAUPTSTADT BAD ISCHL 2024\" - DIE GANZE STADT IST BÜHNE"

    }

    @Test()
    fun `parsing multi day event`() {
        val data = "28.-30. JUNI 2024 \"50 JAHRE-ÖGEG\" Sonderveranstaltung mit Jubiläumsprogramm - 1. SPUR 1-MESSE"
    }

    @Test()
    fun `multiple dates in same string`() {
        val data = "Sonntag, 4., 11., 18. und 25. Juni 2023"
    }
}