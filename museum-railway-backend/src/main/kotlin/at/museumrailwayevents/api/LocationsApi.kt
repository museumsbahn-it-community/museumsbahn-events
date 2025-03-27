package at.museumrailwayevents.api

import at.museumrailwayevents.model.MuseumLocation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/location")
interface LocationsApi {
    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun allLocations(@RequestParam operatorId: String?): ResponseEntity<List<MuseumLocation>>

    @GetMapping(
        "/{locationId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getLocation(@PathVariable locationId: String): ResponseEntity<MuseumLocation>

    @GetMapping(
        "/{locationId}/image/{imageIndex}",
        produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE],
    )
    fun getImage(@PathVariable locationId: String, @PathVariable imageIndex: Int): ResponseEntity<ByteArray>
}