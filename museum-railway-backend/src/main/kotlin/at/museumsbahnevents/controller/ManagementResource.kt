package at.museumsbahnevents.controller

import at.museumsbahnevents.service.DataLoaderService
import jakarta.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path

@Path("/api/manage")
class ManagementResource {

    @Inject
    lateinit var dataLoaderService: DataLoaderService;

    @POST
    @Path("/reloadData")
    fun clearCaches() {
        dataLoaderService.reloadData()
    }
}