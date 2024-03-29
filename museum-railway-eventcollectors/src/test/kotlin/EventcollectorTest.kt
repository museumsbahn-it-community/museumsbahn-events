import assertk.assertThat
import assertk.assertions.isEqualTo
import at.museumrailwayevents.eventcollectors.collectors.NostalgiebahnenKärntenCollector
import at.museumrailwayevents.eventcollectors.collectors.OegegSchmalspurCollector
import at.museumrailwayevents.eventcollectors.collectors.OegegShopCollector
import at.museumrailwayevents.eventcollectors.collectors.WackelsteinexpressCollector
import at.museumrailwayevents.eventcollectors.service.JsoupCrawlerTestMockImpl
import org.junit.jupiter.api.Test

class EventcollectorTest {

    val mockJsoupCrawler = JsoupCrawlerTestMockImpl(this::class.java)

    @Test
    fun `oegeg shop collector should collect 12 events`() {
        val eventcollector = OegegShopCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()

        assertThat(events.size).isEqualTo(12)
        assertThat(events.filter { it.data["location_id"] == "oegeg_normalspur"}.size).isEqualTo(7)
        assertThat(events.filter { it.data["location_id"] == "oegeg_schifffahrt"}.size).isEqualTo(5)
    }

    @Test
    fun `oegeg schmalspurbahn should collect 22 events`() {
        val eventcollector = OegegSchmalspurCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()

        events.map { println(it.startDate) }

        assertThat(events.size).isEqualTo(22)
    }

    @Test
    fun `wackelsteinexpress should collect 23 events`() {
        val eventcollector = WackelsteinexpressCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()

        assertThat(events.size).isEqualTo(23)
    }

    @Test
    fun `nostalgiebahnen in kärnten should collect 18 events`() {
        val eventcollector = NostalgiebahnenKärntenCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(18)
    }
}