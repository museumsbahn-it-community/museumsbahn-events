package eventcollectors

import assertk.assertThat
import assertk.assertions.isEqualTo
import at.museumrailwayevents.eventcollectors.collectors.SteirischeEisenbahnfreundeCollector
import at.museumrailwayevents.eventcollectors.service.JsoupCrawlerTestMockImpl
import at.museumrailwayevents.model.conventions.MuseumEventsCategory
import base.boudicca.SemanticKeys
import org.junit.jupiter.api.Test

class EventcollectorStefTest {

    @Test
    fun testStefWithWebsiteV1() {
        val mockJsoupCrawler = JsoupCrawlerTestMockImpl(
            mapOf(
                "https://www.stef.at/program/" to "steirische_eisenbahnfreunde.html"
            )
        )
        val eventcollector = SteirischeEisenbahnfreundeCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(5)
        assertThat(events.filter { it.data[SemanticKeys.CATEGORY] == MuseumEventsCategory.RAILWAY_MUSEUM.jsonValue}.size).isEqualTo(2)
        assertThat(events.filter { it.data[SemanticKeys.CATEGORY] == MuseumEventsCategory.SPECIAL_TRIP.jsonValue}.size).isEqualTo(3)
    }

    @Test
    fun testStefWithWebsiteV2() {
        // they changed their website during development, so we better test both
        val mockJsoupCrawler = JsoupCrawlerTestMockImpl(
            mapOf(
                "https://www.stef.at/program/" to "steirische_eisenbahnfreunde_2.html"
            )
        )
        val eventcollector = SteirischeEisenbahnfreundeCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(2)
        assertThat(events.filter { it.data[SemanticKeys.CATEGORY] == MuseumEventsCategory.RAILWAY_MUSEUM.jsonValue}.size).isEqualTo(0)
        assertThat(events.filter { it.data[SemanticKeys.CATEGORY] == MuseumEventsCategory.SPECIAL_TRIP.jsonValue}.size).isEqualTo(2)
    }

}