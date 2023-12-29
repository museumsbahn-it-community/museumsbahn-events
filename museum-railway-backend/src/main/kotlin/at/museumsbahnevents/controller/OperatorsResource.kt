package at.museumsbahnevents.controller

import at.museumsbahnevents.api.model.MuseumLocation
import at.museumsbahnevents.api.model.MuseumOperator
import at.museumsbahnevents.service.DataLoaderService
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/api/operator")
class OperatorsResource {

    @Inject
    lateinit var dataLoaderService: DataLoaderService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun allOperators(): List<MuseumOperator> {
        return dataLoaderService.museumOperators
    }
}