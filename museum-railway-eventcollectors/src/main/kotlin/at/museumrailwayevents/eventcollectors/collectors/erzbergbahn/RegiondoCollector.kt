package at.museumrailwayevents.eventcollectors.collectors.erzbergbahn

import at.museumrailwayevents.eventcollectors.collectors.MuseumRailwayEventCollector
import at.museumrailwayevents.eventcollectors.collectors.dateParser.DateParser
import at.museumrailwayevents.eventcollectors.collectors.erzbergbahn.model.Products
import at.museumrailwayevents.eventcollectors.collectors.erzbergbahn.model.Times
import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


// TODO: fix move and make configurable
private const val PLANFAHRTEN = "Planfahrten"


abstract class RegiondoCollector(
    operatorId: String,
    locationId: String,
    sourceUrl: String,
    locationName: String,
    private val eventOverviewUrl: String,
    private val regiondoPartnerCode: String,
) :
    MuseumRailwayEventCollector(
        operatorId,
        locationId,
        sourceUrl,
        locationName = locationName,
    ) {

    // products -> https://shopping-experience-api.prod.regiondo.net/api/v1/products?includeInactive=true&tags=27511
    // times -> https://shopping-experience-api.prod.regiondo.net/api/v1/timeslots/times?productId=216864&numberOfMonths=12

    // test product id: 216864
    //

    // Headers:
    // x-partner-code: VE187 // VE187 is Erzbergbahn
    // x-tenant: REGIONDO
    // x-locale: de-AT
    override fun collectEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val job = GlobalScope.launch {
            val httpClient = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
                install(Logging) {
//                    logger = Logger.DEFAULT
                    level = LogLevel.HEADERS
                    sanitizeHeader { header -> header == HttpHeaders.Authorization }
                }
            }
            val productsUrl =
                "https://shopping-experience-api.prod.regiondo.net/api/v1/products?includeInactive=true"
            val productsResponse =
                httpClient.request(productsUrl) {
                    headers {
                        append("x-partner-code", regiondoPartnerCode)
                        append("x-tenant", "REGIONDO")
                        append("x-locale", "de-AT")
                    }
                }

            val products: Products = productsResponse.body()
            products.items.forEach { product ->

                val productItemsUrl =
                    "https://shopping-experience-api.prod.regiondo.net/api/v1/timeslots/times?productId=${product.id}&numberOfMonths=12"
                val itemsResponse = httpClient.request(productItemsUrl) {
                    headers {
                        append("x-partner-code", regiondoPartnerCode)
                        append("x-tenant", "REGIONDO")
                        append("x-locale", "de-AT")
                        //append("x-partner-domain", "https://vereinerzbergbahn.regiondo.at/")
                    }
                }

                val times = itemsResponse.body<Times>()

                times.days.forEach { day ->
                    day.value.times.forEach { timeEntry ->
                        try {
                            val title = product.title?.replace("Planfahrten", "Erzbergbahn Planfahrten") ?: locationName
                            val date = DateParser.parseIsoDate(day.key, timeEntry.key)
                            val description =
                                requireNotNull(product.description) { "no event description found for $title" }
                            val pictureUrl = product.images.first().url
                            val pictureAltText = product.images.first().alt
                            val recurrence = if (title.contains(PLANFAHRTEN)) {
                                RecurrenceType.REGULARLY
                            } else {
                                RecurrenceType.RARELY
                            }

                            val additionalData = mutableMapOf(
                                SemanticKeys.DESCRIPTION to description,
                                CommonKeys.VEHICLE_TYPE to VehicleType.DIESEL_TRAIN,
                                SemanticKeys.REGISTRATION to MuseumEventRegistration.PRE_SALES_ONLY.jsonValue,
                                SemanticKeys.CATEGORY to MuseumEventsCategory.MUSEUM_RAILWAY.jsonValue,
                                SemanticKeys.RECURRENCE_TYPE to recurrence,
                                SemanticKeys.TAGS to TAGS_MUSEUM_RAILWAY_OPERATING.toTagsValue(),
                            )

                            if (pictureUrl != null) {
                                additionalData[SemanticKeys.PICTURE_URL] = pictureUrl
                            }

                            if (pictureAltText != null) {
                                additionalData[SemanticKeys.PICTURE_ALT_TEXT] = pictureAltText
                            }

                            events.add(
                                createEvent(
                                    title,
                                    date,
                                    eventOverviewUrl,
                                    additionalData
                                )
                            )
                        } catch (ex: Exception) {
                            println("failed for ${day.key} ${timeEntry.key}: ${ex.message}")
                        }
                    }
                }
            }
        }

        runBlocking {
            job.join()
        }

        return events
    }
}