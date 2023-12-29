package at.museumsbahnevents.api.model

data class MuseumOperator(
    val name: String,
    val identifier: String,
    val webUrl: String,
    val description: String,
    val imageName: String,
    val location: Location,
)

