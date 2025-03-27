package at.museumrailwayevents.model

data class MuseumLocation(
        val name: String,
        val shortName: String,
        val type: MuseumType,
        val operatorId: String,
        val locationId: String,
        val webUrl: String,
        val location: Location,
        val eventListUrl: String? = null,
        val googleMapsUrl: String? = null,
        val mapyczUrl: String? = null,
        val geoJsonUrl: String? = null,
        val description: String? = null,
        val eventCollectorType: String? = null,
        val eventCollectionComment: String? = null,
        val tags: List<String> = emptyList(),
        val images: List<ImageSpec> = emptyList(),
        val openingHoursUrl: String?,
)