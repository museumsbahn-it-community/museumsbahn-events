package at.museumrailwayevents.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled("Cannot run in the pipeline because it requires local Google credentials")
@SpringBootTest
@ActiveProfiles("local")
class GoogleDataLoaderServiceTest {

    @Autowired
    lateinit var service: GoogleDataLoaderService

    @Test
    fun `locations and operators should be properly loaded`() {
        println("loaded ${service.museumOperators.size} museum operators")
        println("loaded ${service.museumLocations.size} museum locations")
        assertThat(service.museumOperators.size).isEqualTo(52)
        assertThat(service.museumLocations.size).isEqualTo(63)
    }

}