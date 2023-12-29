package at.museumsbahnevents.api.model

data class MuseumLocation (
    val name: String,
    val type: MuseumType,
    val operatorId: String,
    val eventLocationId: String,
    val webUrl: String,
    val description: String,
    val imageName: String,
    val eventCollectorType: String,
    val eventCollectionNotPossibleReason: String,
    val location: Location,
)