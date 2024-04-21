package at.museumrailwayevents.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "image")
class ImageCachingConfig {
    lateinit var imgProxyUrl: String
    lateinit var signingKey: String
    lateinit var signingSalt: String
    val height = 500
    val width = 500
}