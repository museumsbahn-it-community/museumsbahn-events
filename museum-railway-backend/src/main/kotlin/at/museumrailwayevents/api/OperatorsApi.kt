package at.museumrailwayevents.api

import at.museumrailwayevents.model.MuseumOperator
import io.swagger.annotations.Api
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api("Museum Railway Events Operators API")
@RequestMapping("/api/operator")
interface OperatorsApi {
    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun allOperators(): List<MuseumOperator>
}