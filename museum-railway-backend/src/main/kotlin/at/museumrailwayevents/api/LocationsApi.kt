package at.museumrailwayevents.api

import at.museumrailwayevents.model.MuseumLocation
import io.swagger.annotations.Api
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Museum Railway Events Locations API")
@RequestMapping("/api/location")
interface LocationsApi {
    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun allLocations(@RequestParam operatorId: String?): List<MuseumLocation>
}