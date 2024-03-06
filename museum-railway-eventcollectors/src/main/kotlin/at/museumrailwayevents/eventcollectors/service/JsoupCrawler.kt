package at.museumrailwayevents.eventcollectors.service

import org.jsoup.nodes.Document

interface JsoupCrawler {
    fun getDocument(url: String): Document;
}