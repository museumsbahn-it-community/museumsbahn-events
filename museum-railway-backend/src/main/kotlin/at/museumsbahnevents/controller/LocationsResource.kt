package at.museumsbahnevents.controller

import at.museumsbahnevents.api.model.MuseumLocation
import at.museumsbahnevents.service.DataLoaderService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestQuery

@Path("/api/location")
class LocationsResource {

    @Inject
    lateinit var dataLoaderService: DataLoaderService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun allLocations(@RestQuery operatorId: String?): List<MuseumLocation> {
        val data = dataLoaderService.museumLocations
        if (operatorId != null) {
            return data.filter { it.operatorId == operatorId }
        }
        return data
    }

}
