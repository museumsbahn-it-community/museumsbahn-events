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

    @Test
    fun dateRegexTest() {

        val shouldMatch = listOf(
            "Sa, 8. Juni Diesel-Oldies auf schmaler Spur",
            "Mittwoch, 1. Mai 2024",
            "01.05.2024",
            "So, 5. Mai",
            "Sonntag, 7., 14., 21., und 28. Juli 2024"
        )
        regexTest(DateParser.dateRegex, shouldMatch, emptyList())
    }

    private fun regexTest(regex: Regex, shouldMatch: List<String>, shouldNotMatch: List<String>) {
        shouldMatch.forEach {
            println("$regex should match $it")
            assertThat(regex.containsMatchIn(it)).isTrue()
        }

        shouldNotMatch.forEach {
            println("$regex should not match $it")
            assertThat(regex.containsMatchIn(it)).isFalse()
        }
    }
}