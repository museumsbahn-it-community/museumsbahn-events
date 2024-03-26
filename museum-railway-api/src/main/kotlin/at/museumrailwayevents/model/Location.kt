package at.museumrailwayevents.model

data class Location(
    val lat: Float?,
    val lon: Float?,
    val country: String,
    val state: String,
    val street: String,
    val city: String?,
    val zipCode: String?,
)