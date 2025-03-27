package at.museumrailwayevents.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "google")
class GoogleConfig {
    lateinit var credentialsPath: String
    lateinit var spreadsheetId: String
}