package at.museumrailwayevents.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "image")
class ImageCachingConfig {
    lateinit var imgProxyUrl: String
    lateinit var signingKey: String
    lateinit var signingSalt: String
    val allowedSizes = listOf(256, 512, 1024)
    val height = 512
    val width = 512
}