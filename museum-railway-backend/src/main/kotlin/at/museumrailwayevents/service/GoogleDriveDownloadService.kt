package at.museumrailwayevents.service

import at.museumrailwayevents.config.MuseumRailwayBackendConfig
import at.museumrailwayevents.controller.ImageCachingProxyController
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class GoogleDriveDownloadService(
    private val config: MuseumRailwayBackendConfig,
    private val imageCachingProxyController: ImageCachingProxyController
) {
    fun downloadImage(id: String, size: Int? = null): ByteArray? {
        require(id.matches(sanitizingRegex)) {
            "given id is invalid"
        }

        val baseUrl = config.publicBaseUrl
        val googleUrl = "https://drive.usercontent.google.com/download?id=${id}&export=download"

        // we only access the google url via the imagecache, so that we reduce the load
        return imageCachingProxyController.getImage(googleUrl, size).body
    }

    companion object {
        private val sanitizingRegex = Regex("[a-zA-Z0-9_-]*")
        private val restClient = RestClient.create()
        private val logger = KotlinLogging.logger {}
    }
}