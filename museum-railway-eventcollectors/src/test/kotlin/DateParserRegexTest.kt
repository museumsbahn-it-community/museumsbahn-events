import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import org.junit.jupiter.api.Test

class DateParserRegexTest {

    @Test()
    fun monthRegexText() {
        val shouldMatch = listOf(
            "Jänner",
            "Januar",
            "Februar",
            "März",
            "April",
            "Mai",
            "Juni",
            "Juli",
            "August",
            "September",
            "Oktober",
            "November",
            "Dezember",
            "23. MÄRZ 2024 \"RATSHERRNEXPRESS zum Freistädter Ostermarkt\"",
        )
        val shouldNotMatch = listOf(
            "asdf",
            "testmonth",
            "month"
        )
        regexTest(DateParser.monthWrittenRegex, shouldMatch, shouldNotMatch)
    }

    private fun regexTest(regex: Regex, shouldMatch: List<String>, shouldNotMatch: List<String>) {
        shouldMatch.forEach {
            assertThat(regex.containsMatchIn(it)).isTrue()
        }

        shouldNotMatch.forEach {
            assertThat(regex.containsMatchIn(it)).isFalse()
        }
    }
}