package at.museumrailwayevents.controller

import at.museumrailwayevents.service.DataLoaderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/manage")
@RestController
class ManagementController(private val dataLoaderService: DataLoaderService) {

    @PostMapping("/reloadData")
    fun clearCaches() {
        dataLoaderService.reloadData()
    }
}