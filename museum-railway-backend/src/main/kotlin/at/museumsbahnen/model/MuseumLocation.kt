package at.museumsbahnen.model

data class MuseumLocation(
    val name: String,
    val shortName: String? = null,
    val location: Location,
    val webUrl: String,
    val description: String,
    val imageName: String,
)

data class Location(
    val lat: Float,
    val lon: Float,
    val country: String,
    val state: String,
    val street: String,
    val city: String?,
    val zipCode: String?,
)