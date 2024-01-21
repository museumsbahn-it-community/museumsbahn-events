package at.museumrailwayevents.controller

import at.museumrailwayevents.service.DataLoaderService
import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api("Museum Railway Events Management API")
@RequestMapping("/api/manage")
@RestController
class ManagementController(private val dataLoaderService: DataLoaderService) {

    @PostMapping("/reloadData")
    fun clearCaches() {
        dataLoaderService.reloadData()
    }
}