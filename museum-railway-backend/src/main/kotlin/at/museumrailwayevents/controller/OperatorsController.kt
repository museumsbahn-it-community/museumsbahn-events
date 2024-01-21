package at.museumsbahnevents.controller

import at.museumsbahnevents.api.OperatorsApi
import at.museumsbahnevents.model.MuseumOperator
import at.museumsbahnevents.service.DataLoaderService
import org.springframework.web.bind.annotation.RestController


@RestController
class OperatorsController(private val dataLoaderService: DataLoaderService) : OperatorsApi {
    override fun allOperators(): List<MuseumOperator> {
        return dataLoaderService.museumOperators
    }
}