package at.museumrailwayevents.eventcollectors.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class JsoupCrawlerTestMockImpl(val contextClass: Class<out Any>) : JsoupCrawler {

    val wackelsteinexpressDetailsMap = mapOf(
            "https://reservierung.wackelsteinexpress.at/produkt/osterhasenzug-30-maerz-1000-uhr/" to "wackelsteinexpress/wackelsteinexpress_03-30_10_00-Osterhasenzug 30. März 10_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/osterhasenzug-30-maerz-1400-uhr/" to "wackelsteinexpress/wackelsteinexpress_03-30_14_00-Osterhasenzug 30. März 14_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/osterhasenzug-30-maerz-1600-uhr/" to "wackelsteinexpress/wackelsteinexpress_03-30_16_00-Osterhasenzug 30. März 16_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/teddybaerzug-15-juni-1500-uhr/" to "wackelsteinexpress/wackelsteinexpress_03-30_16_00-Osterhasenzug 30. März 16_00 Uhr.html",// todo
            "https://reservierung.wackelsteinexpress.at/produkt/weinglasblaeserzug-19-juli-1900-uhr/" to "wackelsteinexpress/wackelsteinexpress_07-19_19_00-Weinglasbläserzug 19. Juli 19_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/fruehstuecksfahrt-21-juli-0945-uhr/" to "wackelsteinexpress/wackelsteinexpress_07-21_09_45_Frühstücksfahrt 21. Juli 09_45 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/radwandern-10-august-1115-uhr/" to "wackelsteinexpress/wackelsteinexpress_08-10_11_15-Radwandern 10. August 11_15 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/mondscheinfahrt-16-august-2030-uhr/" to "wackelsteinexpress/wackelsteinexpress_08-16_20_30-Mondscheinfahrt 16. August 20_30 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/mondscheinfahrt-17-august-2030-uhr/" to "wackelsteinexpress/wackelsteinexpress_08-17_20_30-Mondscheinfahrt 17. August 20_30 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/glasblaeserzug-21-august-1345-uhr/" to "wackelsteinexpress/wackelsteinexpress_08-21_13_45_Glasbläserzug 21. August 13_45 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/zaubererzug-13-mai-1400-uhr/" to "wackelsteinexpress/wackelsteinexpress_09-21_14_00-Zauber-Express 21. September 14_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/heurigen-express-01-oktober-1500-uhr/" to "wackelsteinexpress/wackelsteinexpress_10-12_15_00-Heurigen-Express 12. Oktober 15_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/waldgeisterzug-26-oktober-1400-uhr/" to "wackelsteinexpress/wackelsteinexpress_10-26_14_00-Waldgeisterzug 26. Oktober 14_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/waldgeisterzug-27-oktober-1400-uhr-kopie/" to "wackelsteinexpress/wackelsteinexpress_10-27_14_00-Waldgeisterzug 27. Oktober 14_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/nikolozug-2-dezember-1000-uhr/" to "wackelsteinexpress/wackelsteinexpress_12-07_10_00-Nikolozug 7. Dezember 10_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/nikolozug-2-dezember-1400-uhr-kopie/" to "wackelsteinexpress/wackelsteinexpress_12-07_14_00-Nikolozug 7. Dezember 14_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/nikolozug-3-dezember-1000-uhr-kopie/" to "wackelsteinexpress/wackelsteinexpress_12-08_10_00-Nikolozug 8. Dezember 10_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/nikolozug-3-dezember-1400-uhr/" to "wackelsteinexpress/wackelsteinexpress_12-08_14_00-Nikolozug 8. Dezember 14_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/adventlichterfahrt-14-dezember-1700-uhr/" to "wackelsteinexpress/wackelsteinexpress_12-14_17_00-Adventlichterfahrt 14. Dezember 17_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/bratapfelzug-18-dezember-1400-uhr/" to "wackelsteinexpress/wackelsteinexpress_12-15_14_00-Bratapfelzug 15. Dezember 14_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/lebkuchen-express-17-dezember-1400-uhr/" to "wackelsteinexpress/wackelsteinexpress_12-22_14_00-Lebkuchen-Express 22. Dezember 14_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/christkindlzug-24-dezember-1000-uhr/" to "wackelsteinexpress/wackelsteinexpress_12-24_10_00-Christkindlzug 24. Dezember 10_00 Uhr.html",
            "https://reservierung.wackelsteinexpress.at/produkt/christkindlzug-24-dezember-1330-uhr/" to "wackelsteinexpress/wackelsteinexpress_12-24_13_30-Christkindlzug 24. Dezember 13_30 Uhr.html",
    )

    val pathMap = mapOf(
            "https://www.oegeg.at/termine/termine-normalspur-museum-lokpark-ampflwang/" to "oegeg_normalspur.htm",
            "https://www.oegeg.at/termine/termine-schifffahrt-dampfschiff-sch%C3%B6nbrunn/" to "oegeg_schifffahrt.htm",
            "https://www.oegeg.at/termine/termine-schmalspur-steyrtalbahn/" to "oegeg_schmalspurbahn.htm",
            "https://www.wackelsteinexpress.at/fahrplan/" to "wackelsteinexpress/wackelsteinexpress_planzuege.htm",
            "https://reservierung.wackelsteinexpress.at/" to "wackelsteinexpress/wackelsteinexpress_page1.htm",
            "https://reservierung.wackelsteinexpress.at/page/2/" to "wackelsteinexpress/wackelsteinexpress_page2.htm",
    ) + wackelsteinexpressDetailsMap


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