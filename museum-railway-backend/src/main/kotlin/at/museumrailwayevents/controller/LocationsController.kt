package at.museumrailwayevents.controller

import at.museumrailwayevents.api.LocationsApi
import at.museumrailwayevents.model.MuseumLocation
import at.museumrailwayevents.service.GoogleDataLoaderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class LocationsController(private val dataLoaderService: GoogleDataLoaderService) : LocationsApi {

    override fun allLocations(operatorId: String?): ResponseEntity<List<MuseumLocation>> {
        val data = dataLoaderService.museumLocations
        if (operatorId != null) {
            return ResponseEntity.ok(data.filter { it.operatorId == operatorId })
        }
        return ResponseEntity.ok(data)
    }

    override fun getLocation(locationId: String): ResponseEntity<MuseumLocation> {
        val location = dataLoaderService.museumLocations.find { it.locationId == locationId }
        if (location == null) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(location)
    }

    override fun getImage(locationId: String, imageIndex: Int): ResponseEntity<ByteArray> {
        val image = dataLoaderService.loadImageForLocation(locationId, imageIndex) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(image)
    }

}
