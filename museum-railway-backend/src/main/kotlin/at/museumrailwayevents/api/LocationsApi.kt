package at.museumrailwayevents.api

import at.museumrailwayevents.model.MuseumLocation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/location")
interface LocationsApi {
    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun allLocations(@RequestParam operatorId: String?): List<MuseumLocation>
}