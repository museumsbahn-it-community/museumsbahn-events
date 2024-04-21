package at.museumrailwayevents.eventcollectors.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class JsoupCrawlerTestMockImpl(val overrideMap: Map<String, String>? = null) :
    JsoupCrawler {

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

    val oeglbMap = mapOf(
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/fahrplan/fahrplan-oktober/" to "oeglb/hoellentalbahn-fahrplan-oktober.html",
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/fahrplan/fahrplan-sommer/" to "oeglb/hoellentalbahn-fahrplan-sommer.html",
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/sonderveranstaltungen/lange-nacht-der-museen/" to "oeglb/hoellentalbahn-sonderveranstaltung-lange-nacht.html",
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/sonderveranstaltungen/" to "oeglb/hoellentalbahn-sonderveranstaltungen.html",
        "https://www.lokalbahnen.at/hoellentalbahn/mitfahren/sonderveranstaltungen/mondweinfahrten/" to "oeglb/hoellentalbahn-sonderveranstaltung-mondweinfahrt.html",
        "https://www.lokalbahnen.at/bergstrecke/mitfahren/fahrplan/" to "oeglb/ybbstalbahn-fahrplan.html",
        "https://www.lokalbahnen.at/bergstrecke/mitfahren/sonderveranstaltungen/" to "oeglb/ybbstalbahn-sonderveranstaltungen.html",
    )

    val waldviertelbahnMap = mapOf(
        "https://www.waldviertelbahn.at/nostalgie-diesel-1" to "noevog/waldviertelbahn_diesel.htm",
        "https://www.waldviertelbahn.at/nostalgie-dampf" to "noevog/waldviertelbahn_dampf.htm",
        // waldviertelbahn dampf
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-nostalgie-dampf-gmuend-gross-gerungs" to "noevog/waldviertelbahn-dampf/wvb_dampf_gmünd_groß-gerungs.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-nostalgie-dampf-gmuend-litschau" to "noevog/waldviertelbahn-dampf/wvb_dampf_gmünd_litschau.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-schnitzelexpress" to "noevog/waldviertelbahn-dampf/wvb_dampf_schnitzelexpress.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-bier-hopfenexpress" to "noevog/waldviertelbahn-dampf/wvb_dampf_bier-hopfenexpress.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-ueberfall-westernzug" to "noevog/waldviertelbahn-dampf/wvb_dampf_westernzug.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-dorfwirtexpress" to "noevog/waldviertelbahn-dampf/wvb_dampf_dorfwirtexpress.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-dampf-alpakaexpress" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-schrammelklangexpress" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-kistensau-express?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-dampfzug-mit-oldtimern-auf-strasse-und-schiene?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-erdaepfelexpress?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-karpfenexpress?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-waldviertler-gulaschzug?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-silvesterzug-mit-volldampf-ins-jahr-2024" to "noevog/waldviertelbahn-dampf/wvb_dampf_silvesterzug.htm",
        // waldviertelbahn diesel
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-nostalgie-diesel-gmuend-litschau" to "noevog/waldviertelbahn-diesel/wvb_diesel_gmünd_litschau.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-rad-bahn-ueber-den-waldviertler-semmering" to "noevog/waldviertelbahn-diesel/wvb_diesel_rad_bahn.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-huetehundezug" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-nostalgie-diesel-gmuend-gross-gerungs" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-hammerschmiedezug-gopprechts" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-zaubererzug" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-waldviertler-musikantenexpress" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-mohnnudelzug" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-dampfzug-mit-oldtimern-mopdes-und-motorraeder?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-schloss-litschau-exklusiv?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-candle-light-train?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-knoedelexpress?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-alpakaexpress-zum-sonnseitn-hof?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-schafkaeseexpress?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-mit-bahn-und-zu-fuss-ueber-den-waldviertler-semmering?dateFrom=" to "noevog/waldviertelbahn-diesel/wvb_diesel_semmering.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-martiniganslexpress?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-sonderzuege-zum-weitraer-advent?dateFrom=" to "noevog/dummy-page.htm",
        "https://www.waldviertelbahn.at/angebote-fuer-fahrgaeste-wvb/o-christkindlzug-1" to "noevog/waldviertelbahn-diesel/wvb_diesel_christkindlzug.htm",
    )

    val reblausexpressMap = mapOf(
        "https://www.reblausexpress.at/tickets-angebote-reb" to "noevog/reblausexpress/reblausexpress_angebote.html",
        "https://www.reblausexpress.at/angebote-reblaus-express-1/o-tickets-reblaus-express" to "noevog/reblausexpress/reblausexpress_ticket.html",
        "https://www.reblausexpress.at/angebote-reblaus-express-1/o-aktion-saisonkarte-50-reb" to "noevog/reblausexpress/reblausexpress_saisonkarte.html",
        "https://www.reblausexpress.at/angebote-reblaus-express-1/o-retzer-erlebniskeller-1" to "noevog/reblausexpress/reblausexpress_retzer_erlebniskeller.html",
        "https://www.reblausexpress.at/angebote-reblaus-express-1/o-stadtfuehrung-drosendorf" to "noevog/reblausexpress/reblausexpress_stadtfuerhung_drosendorf.html",
        "https://www.reblausexpress.at/angebote-reblaus-express-1/o-muttertag-am-reblaus-express" to "noevog/reblausexpress/reblausexpress_muttertag.html",
        "https://www.reblausexpress.at/angebote-reblaus-express-1/o-nostalgiekirtag-wie-damals-im-waldviertel" to "noevog/reblausexpress/reblausexpress_nostalgiekirtag.html",
        "https://www.reblausexpress.at/angebote-reblaus-express-1/o-pleissinger-wunschlauf" to "noevog/reblausexpress/reblausexpress_wunschlauf.html",
        "https://www.reblausexpress.at/angebote-reblaus-express-1/o-erdaepfelfest-in-geras" to "noevog/reblausexpress/reblausexpress_erdaepfelfest.html",
    )

    val pathMap = mapOf(
        "https://www.oegeg.at/termine/termine-normalspur-museum-lokpark-ampflwang/" to "oegeg_normalspur.htm",
        "https://www.oegeg.at/termine/termine-schifffahrt-dampfschiff-sch%C3%B6nbrunn/" to "oegeg_schifffahrt.htm",
        "https://www.oegeg.at/termine/termine-schmalspur-steyrtalbahn/" to "oegeg_schmalspurbahn.htm",
        "https://www.wackelsteinexpress.at/fahrplan/" to "wackelsteinexpress/wackelsteinexpress_planzuege.htm",
        "https://reservierung.wackelsteinexpress.at/" to "wackelsteinexpress/wackelsteinexpress_page1.htm",
        "https://reservierung.wackelsteinexpress.at/page/2/" to "wackelsteinexpress/wackelsteinexpress_page2.htm",
        "https://www.nostalgiebahn.at/termine.html" to "nostalgiebahn_kaernten/nostalgiebahn_kaernten_termine.html",
        "https://www.reblausexpress.at/fahrplan-reb-express" to "reblausexpress",
        "https://www.waldviertelbahn.at/fahrplan-wvb-gesamt" to "waldviertelbahn",
        "http://www.mh6.at/de/termine/" to "mh6_termine.htm",
        "http://www.mh6.at/de/besuchen-sie-uns-beim-anbrennen-der-mh-6-8-2/" to "mh6_anbrennen.htm",
        "https://eisenbahnmuseum.at/veranstaltungen/termine/" to "ebm_schwechat",
        "https://www.steiermarkbahn.at/reisen-freizeit/murtalbahn-dampfzug/" to "murtalbahn",
        "https://ebfl.at/index.php/suedbahn-heizhaus/" to "ebfl/ebfl_suedbahn_museum.htm",
        "https://ebfl.at/index.php/suedbahn-express/" to "ebfl/ebfl_overview.htm",
        "https://ebfl.at/index.php/tirolfahrt-2024-2/" to "ebfl/ebfl_event1.htm",
        "https://ebfl.at/index.php/fahrt-an-den-woerthersee/" to "ebfl/ebfl_event2.htm",
        "https://ebfl.at/index.php/christkindlfahrt-graz-2024/" to "ebfl/ebfl_event3.htm",
        "https://eisenbahnmuseum.at/veranstaltungen/termine/" to "ebm_schwechat.htm",
    ) + wackelsteinexpressDetailsMap + oeglbMap + waldviertelbahnMap + reblausexpressMap


    override fun getDocument(url: String): Document {
        val usedMap = overrideMap ?: pathMap

        if (!usedMap.containsKey(url)) {
            throw Error("no mock mapping exists for url: $url")
        }

        val path = "examples/${usedMap[url]}"
        val pageData = javaClass.classLoader.getResource(path)?.readText()
        requireNotNull(pageData) {
            println("no page found for url: $url")
        }
        return Jsoup.parse(pageData)
    }
}