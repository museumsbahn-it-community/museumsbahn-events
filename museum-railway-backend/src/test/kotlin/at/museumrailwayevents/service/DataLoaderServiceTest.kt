package at.museumrailwayevents.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class DataLoaderServiceTest {

    @Test
    fun `locations and operators should be properly loaded`() {
        val service = DataLoaderService()

        println("loaded ${service.museumOperators.size} museum operators")
        println("loaded ${service.museumLocations.size} museum locations")
        assertThat(service.museumOperators.size).isEqualTo(41)
        assertThat(service.museumLocations.size).isEqualTo(55)
    }

}