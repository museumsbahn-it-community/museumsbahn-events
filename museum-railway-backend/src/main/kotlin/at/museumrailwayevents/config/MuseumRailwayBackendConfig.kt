package at.museumrailwayevents.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "museum-railway-backend")
class MuseumRailwayBackendConfig {
    // public base url is required so we can generate valid urls for the museum images
    lateinit var publicBaseUrl: String
}