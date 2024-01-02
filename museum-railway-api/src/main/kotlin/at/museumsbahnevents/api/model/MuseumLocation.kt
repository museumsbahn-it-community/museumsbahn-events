package at.museumsbahnevents.api.model

data class MuseumLocation (
    val name: String,
    val type: MuseumType,
    val operatorId: String,
    val locationId: String,
    val webUrl: String,
    val description: String,
    val imageName: String,
    val eventCollectorType: String,
    val eventCollectionNotPossibleReason: String,
    val location: Location,
)