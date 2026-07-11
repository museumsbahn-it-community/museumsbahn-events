package at.museumrailwayevents.eventcollectors.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class JsoupCrawlerImpl: JsoupCrawler {
    override fun getDocument(url: String): Document = Jsoup.connect(url).get();
}