package at.museumrailwayevents.eventcollectors.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class JsoupCrawlerTestMockImpl(val contextClass: Class<out Any>) : JsoupCrawler {

    val pathMap = mapOf(
            "https://www.oegeg.at/termine/termine-normalspur-museum-lokpark-ampflwang/" to "oegeg_normalspur.htm",
            "https://www.oegeg.at/termine/termine-schifffahrt-dampfschiff-sch%C3%B6nbrunn/" to "oegeg_schifffahrt.htm",
            "https://www.oegeg.at/termine/termine-schmalspur-steyrtalbahn/" to "oegeg_schmalspurbahn.htm",
            "https://www.wackelsteinexpress.at/fahrplan/" to "wackelsteinexpress_planzuege.htm",
            "https://reservierung.wackelsteinexpress.at/" to "wackelsteinexpress_page1.htm"
    )

    override fun getDocument(url: String): Document {
        if (!pathMap.containsKey(url)) {
            throw Error("no mock mapping exists for url: $url")
        }

        val path = "examples/${pathMap[url]}"
        val pageData = contextClass.getResource(path)?.readText()
        requireNotNull(pageData)
        return Jsoup.parse(pageData)
    }
}