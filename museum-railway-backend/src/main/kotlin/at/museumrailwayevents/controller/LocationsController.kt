package at.museumsbahnevents.controller

import at.museumsbahnevents.api.LocationsApi
import at.museumsbahnevents.model.MuseumLocation
import at.museumsbahnevents.service.DataLoaderService
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
