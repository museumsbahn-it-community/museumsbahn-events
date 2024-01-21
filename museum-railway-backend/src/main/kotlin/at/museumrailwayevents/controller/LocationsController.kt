package at.museumrailwayevents.controller

import at.museumrailwayevents.api.LocationsApi
import at.museumrailwayevents.model.MuseumLocation
import at.museumrailwayevents.service.DataLoaderService
import org.springframework.web.bind.annotation.RestController

@RestController
class LocationsController(private val dataLoaderService: DataLoaderService) : LocationsApi {

    override fun allLocations(operatorId: String?): List<MuseumLocation> {
        val data = dataLoaderService.museumLocations
        if (operatorId != null) {
            return data.filter { it.operatorId == operatorId }
        }
        return data
    }

}
