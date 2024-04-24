import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import at.museumrailwayevents.eventcollectors.collectors.*
import at.museumrailwayevents.eventcollectors.service.JsoupCrawlerTestMockImpl
import at.museumrailwayevents.model.conventions.CATEGORY_MUSEUM_TRAIN
import at.museumrailwayevents.model.conventions.CATEGORY_RAILWAY_MUSEUM
import at.museumrailwayevents.model.conventions.CommonKeys
import at.museumrailwayevents.model.conventions.Tags
import base.boudicca.SemanticKeys
import org.junit.jupiter.api.Test
import java.time.Month
import kotlin.math.exp

class EventcollectorTest {

    val mockJsoupCrawler = JsoupCrawlerTestMockImpl()

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


    @Test
    fun `ybbstalbahn should collect 39 events`() {
        val eventcollector = YbbstalbahnCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(34)
    }

    @Test
    fun `hoellentalbahn should collect 17 events`() {
        val eventcollector = HoellentalbahnCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(17)

        assertThat(events.filter { it.startDate.month == Month.MAY }.size).isEqualTo(0)
        assertThat(events.filter { it.startDate.month == Month.JUNE }.size).isEqualTo(1)
        assertThat(events.filter { it.startDate.month == Month.JULY }.size).isEqualTo(4)
        assertThat(events.filter { it.startDate.month == Month.AUGUST }.size).isEqualTo(5)
        assertThat(events.filter { it.startDate.month == Month.SEPTEMBER }.size).isEqualTo(1)
        assertThat(events.filter { it.startDate.month == Month.OCTOBER }.size).isEqualTo(6)

        assertThat(events.filter { it.name.contains("Mondweinfahrt") }.size).isEqualTo(1)
        assertThat(events.filter { it.name.contains("Lange Nacht der Museen") }.size).isEqualTo(1)
    }

    @Test
    fun `ebfl should collect 4 events`() {
        val eventcollector = EbflCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(4)
    }

    @Test
    fun `mh6 should collect 9 events`() {
        val eventcollector = Mh6Collector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(9)
    }
    @Test
    fun `waldviertelbahn should collect 33 events`() {
        val eventcollector = WaldviertelbahnCollector(mockJsoupCrawler)
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(33)
    }

    @Test
    fun `reblausexpress should collect 55 events`() {
        val eventcollector = ReblausexpressCollector(mockJsoupCrawler)
        val expectedNumberOfEvents = 55
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(expectedNumberOfEvents)

        // some events should be filtered out to not spam the site
        assertThat(events.filter { it.name.contains("Saisonkarte")}).isEmpty()
        assertThat(events.filter { it.name.contains("Wunschlauf")}).isEmpty()
        assertThat(events.filter { it.name.contains("Erdäpfelfest")}).isEmpty()

        // "Ticket" should be removed from title
        assertThat(events.filter { it.name.contains("Ticket")}).isEmpty()
        assertThat(events.filter { it.name.contains("Reblaus Express")}.size).isEqualTo(expectedNumberOfEvents - 1)

    }

    @Test
    fun `ebm schwechat should collect 13 events`() {
        val eventcollector = EbmSchwechatCollector(mockJsoupCrawler)
        val expectedNumberOfEvents = 13
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(expectedNumberOfEvents)

        assertThat(events.filter { it.data.containsKey(SemanticKeys.PICTURE_URL) }.size).isEqualTo(5)
    }

    @Test
    fun `mlv zwettl should collect 10 events`() {
        val eventcollector = MLVZwettlCollector(mockJsoupCrawler)
        val expectedNumberOfEvents = 10
        val events = eventcollector.collectEvents()
        assertThat(events.size).isEqualTo(expectedNumberOfEvents)

        assertThat(events.filter { it.data.containsValue(CATEGORY_MUSEUM_TRAIN) }.size).isEqualTo(6)
        assertThat(events.filter { it.data.containsValue(CATEGORY_RAILWAY_MUSEUM) }.size).isEqualTo(4)
        assertThat(events.filter { it.data.containsValue(Tags.LOCOMOTIVE_TYPE_STEAM) }.size).isEqualTo(5)
    }


}