import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import org.junit.jupiter.api.Test

class TimeParserRegexTest {

    @Test()
    fun timeRegexTest() {
        val shouldMatch = listOf(
            "0:00",
            "00:00",
            "01:00",
            "23:59",
        ).map { it.lowercase() }
        val shouldNotMatch = listOf(
            "November",
            "Dezember",
            "23. MÄRZ 2024 \"RATSHERRNEXPRESS zum Freistädter Ostermarkt\"",
            "an Sonntagen vom 2. Juli bis 1. Oktober 2023",
            "asdf",
            "testmonth",
            "month",
            "landschaft und weinverkostung im inneren des schwarza-viaduktes der semmeringbahn am 17. 8. 2024.\n" +
                    "reservierungen können per e-mail unter hoellentalbahn@lokalbahnen.at vorgenommen werden.\n" +
                    "\n" +
                    "zur übersicht über alle sonderveranstaltungen"
        )
        regexTest(DateParser.timeRegex, shouldMatch, shouldNotMatch)
    }

    private fun regexTest(regex: Regex, shouldMatch: List<String>, shouldNotMatch: List<String>) {
        shouldMatch.forEach {
            assertThat(regex.containsMatchIn(it), name = "$regex should match $it").isTrue()
        }

        shouldNotMatch.forEach {
            assertThat(regex.containsMatchIn(it), name = "$regex should not match $it").isFalse()
        }
    }

}