package at.museumrailwayevents.eventcollectors.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class JsoupCrawlerImpl: JsoupCrawler {
    override fun getDocument(url: String): Document = Jsoup.connect(url).get();
}