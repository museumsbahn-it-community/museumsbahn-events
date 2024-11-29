package at.museumrailwayevents.model

data class MuseumLocation (
        val name: String,
        val shortName: String,
        val type: MuseumType,
        val operatorId: String,
        val locationId: String,
        val webUrl: String,
        val description: String,
        val imageName: String,
        val eventCollectorType: String,
        val eventCollectionComment: String,
        val location: Location,
)