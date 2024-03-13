import assertk.assertThat
import assertk.assertions.isEqualTo
import at.museumrailwayevents.eventcollectors.collectors.OegegSchmalspurCollector
import at.museumrailwayevents.eventcollectors.collectors.OegegShopCollector
import at.museumrailwayevents.eventcollectors.collectors.WackelsteinexpressCollector
import at.museumrailwayevents.eventcollectors.service.JsoupCrawlerTestMockImpl
import org.junit.jupiter.api.Test

class EventcollectorTest {

    val mockJsoupCrawler = JsoupCrawlerTestMockImpl(this::class.java)

    @Test
    fun `oegeg shop collector should collect 10 events`() {
        val eventcollector = OegegShopCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()

        assertThat(events.size).isEqualTo(10)
        assertThat(events.filter { it.data["location_id"] == "oegeg_normalspur"}.size).isEqualTo(6)
        assertThat(events.filter { it.data["location_id"] == "oegeg_schifffahrt"}.size).isEqualTo(4)
    }

    @Test
    fun `oegeg schmalspurbahn should collect 22 events`() {
        val eventcollector = OegegSchmalspurCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()

        events.map { println(it.startDate) }

        assertThat(events.size).isEqualTo(22)
    }

    @Test
    fun `wackelsteinexpress should collect asdf events`() {
        val eventcollector = WackelsteinexpressCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()

        assertThat(events.size).isEqualTo(23)
    }
}