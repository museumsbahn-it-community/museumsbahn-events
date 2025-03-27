package at.museumrailwayevents.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Service
class GoogleDriveDownloadService {
    fun downloadImage(id: String): ByteArray? {
        require(id.matches(sanitizingRegex)) {
            "given id is invalid"
        }

        val baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        val googleUrl = "https://drive.usercontent.google.com/download?id=${id}&export=download"

        // we only access the google url via the imagecache, so that we reduce the load
        val url = "${baseUrl}/imgcache?url=${googleUrl}"

        val response = restClient.get()
            .uri(url)
            .retrieve()
            .onStatus { status ->
                when (status.statusCode) {
                    HttpStatus.OK -> false
                    HttpStatus.NOT_FOUND -> {
                        logger.warn { "requested image could not be found: id: $id" }
                        true
                    }

                    else -> {
                        logger.warn { "error requesting image from google drive: id: $id" }
                        true
                    }
                }
            }

        return response.body()
    }

    companion object {
        private val sanitizingRegex = Regex("[a-zA-Z0-9_-]*")
        private val restClient = RestClient.create()
        private val logger = KotlinLogging.logger {}
    }
}