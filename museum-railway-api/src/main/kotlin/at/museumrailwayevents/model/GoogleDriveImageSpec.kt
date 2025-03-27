package at.museumrailwayevents.model
import kotlinx.serialization.Serializable

@Serializable
data class GoogleDriveImageSpec(
    val googleDriveId: String,
    val copyright: String,
    val alt: String,
)
