import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import org.junit.jupiter.api.Test

class DateParserRegexTest {

    @Test()
    fun monthWrittenRegexTest() {
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
                "an Sonntagen vom 2. Juli bis 1. Oktober 2023",
        ).map { it.lowercase() }
        val shouldNotMatch = listOf(
                "asdf",
                "testmonth",
                "month"
        )
        regexTest(DateParser.monthWrittenRegex, shouldMatch, shouldNotMatch)
    }

    @Test()
    fun monthCombinedRegexTest() {
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
                "01.01.",
                "31.02.",
                "16.03.",
                "1.04.",
                "26.05.",
                "01.1.",
                "01.2.",
                "1.3.",
                "01.4.",
                "1.5.",
                "01.10.",
                "01.11.",
                "01.12.",
                "16.12.",
                "16. 12.",
                "1.1.2024",
                "an Sonntagen vom 2. Juli bis 1. Oktober 2023",
        ).map { it.lowercase() }
        val shouldNotMatch = listOf(
                "13",
                //"01.13.", // TODO: fix regex; somehow this currently fails because otherwise days would not be recognized correctly
                "0.",
                "0",
                "asdf",
                "testmonth",
                "month"
        )
        regexTest(DateParser.monthRegex, shouldMatch, shouldNotMatch)
    }

    @Test
    fun `numeric day regex match test`() {
        val shouldMatch = listOf(
                "Sa, 8. Juni Diesel-Oldies auf schmaler Spur",
                "Mittwoch, 1. Mai 2024",
                "01.05.2024",
                "So, 5. Mai",
                "Sonntag, 7., 14., 21., und 28. Juli 2024",
                "07.06."
        ).map{it.lowercase()}
        val shouldNotMatch = listOf(
                "",
                "0",
        ).map{it.lowercase()}
        regexTest(DateParser.numericDayRegex, shouldMatch, shouldNotMatch)
    }

    @Test
    fun durationStringRegexTest() {
        val shouldMatch = listOf(
                "Planzüge jeden Mittwoch, Samstag, Sonn- und Feiertag vom 17.07.2024 bis 28.08.2024",
                "an Sonntagen vom 2. Juli bis 1. Oktober 2023",
                "Donnerstag, 30. Mai bis Sonntag, 02. Juni",
                "Freitag und Samstag von 07.06. bis 29.06.2024",
                "Täglich vom 30.05. bis 02.06.2024",
//                "02.07. - 08.09.2024",
//                "25.10. - 31.10.2024",
//                "Freitag und Samstag von 13.09. - 19.10.2024",
        ).map { it.lowercase() }

        val shouldNotMatch = listOf(
                "Sa, 8. Juni Diesel-Oldies auf schmaler Spur",
                "Mittwoch, 1. Mai 2024",
                "01.05.2024",
                "So, 5. Mai",
                "Sonntag, 7., 14., 21., und 28. Juli 2024"
        ).map{it.lowercase()}

        regexTest(DateParser.durationStringRegex, shouldMatch, shouldNotMatch)
    }

    @Test
    fun `full date regex should match n times test`() {
        val expectedMatches = mapOf(
                "Planzüge jeden Mittwoch, Samstag, Sonn- und Feiertag vom 17.07.2024 bis 28.08.2024" to 2,
                "an Sonntagen vom 2. Juli bis 1. Oktober 2023" to 2,
                "Sa, 8. Juni Diesel-Oldies auf schmaler Spur" to 1,
                "Mittwoch, 1. Mai 2024" to 1,
                "01.05.2024" to 1,
                "So, 5. Mai" to 1,
                "Sonntag, 7., 14., 21., und 28. Juli 2024" to 1,
                "1. Oktober 2023" to 1,
        ).mapKeys { it.key.lowercase() }

        regexCountTest(DateParser.fullDateRegex, expectedMatches)
    }

    private fun regexTest(regex: Regex, shouldMatch: List<String>, shouldNotMatch: List<String>) {
        shouldMatch.forEach {
            assertThat(regex.containsMatchIn(it), name = "$regex should match $it").isTrue()
        }

        shouldNotMatch.forEach {
            assertThat(regex.containsMatchIn(it), name = "$regex should not match $it").isFalse()
        }
    }

    private fun regexCountTest(regex: Regex, expectedMatches: Map<String, Int>) {
        expectedMatches.forEach { (text, expectedNumberOfMatches) ->
            assertThat(
                    regex.findAll(text).count(),
                    name = "$regex should match \"$text\" $expectedNumberOfMatches times")
                    .isEqualTo(expectedNumberOfMatches)
        }
    }
}