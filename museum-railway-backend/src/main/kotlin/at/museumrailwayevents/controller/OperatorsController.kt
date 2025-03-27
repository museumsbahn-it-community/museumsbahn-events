package at.museumrailwayevents.controller

import at.museumrailwayevents.api.OperatorsApi
import at.museumrailwayevents.model.MuseumOperator
import at.museumrailwayevents.service.GoogleDataLoaderService
import org.springframework.web.bind.annotation.RestController


@RestController
class OperatorsController(private val dataLoaderService: GoogleDataLoaderService) : OperatorsApi {
    override fun allOperators(): List<MuseumOperator> {
        return dataLoaderService.museumOperators
    }
}