package at.museumrailwayevents.eventcollectors.collectors.erzbergbahn.model

import kotlinx.serialization.Serializable

@Serializable
data class Products(
    var items: ArrayList<Items> = arrayListOf(),
    var totalItemCount: Int? = null,
    var offset: Int? = null,
    var limit: Int? = null
)

@Serializable
data class Images(
    var url: String? = null,
    var alt: String? = null,
    var thumbnailUrl: String? = null
)


@Serializable
data class Geolocation(
    var latitude: String? = null,
    var longitude: String? = null
)

@Serializable
data class Locations(
    var city: String? = null,
    var distance: Int? = null,
    var geolocation: Geolocation? = Geolocation()
)

@Serializable
data class Gross(
    var amount: String? = null,
    var currencyCode: String? = null
)


@Serializable
data class Price(
    var gross: Gross? = Gross()
)


@Serializable
data class Rating(
    var count: Int? = null,
    var value: Int? = null
)

@Serializable
data class Duration(
    var unit: String? = null,
    var value: String? = null
)

@Serializable
data class Items(
    var id: String? = null,
    var images: ArrayList<Images> = arrayListOf(),
    var locations: ArrayList<Locations> = arrayListOf(),
    var price: Price? = Price(),
    var rating: Rating? = Rating(),
    var sellOption: String? = null,
    var title: String? = null,
    var description: String? = null,
    var duration: ArrayList<Duration> = arrayListOf(),
    var type: String? = null
)









