package at.museumrailwayevents.controller

import at.museumrailwayevents.api.ImageCachingProxyApi
import at.museumrailwayevents.config.ImageCachingConfig
import at.museumrailwayevents.service.ImgproxyUrlSigningService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull

@RestController
class ImageCachingProxyController(
    private val imageCachingConfig: ImageCachingConfig,
    private val signingService: ImgproxyUrlSigningService,
) : ImageCachingProxyApi {

    private val cache = ConcurrentHashMap<String, CacheEntry>()

    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    override fun getImage(url: String, size: Int?): ResponseEntity<ByteArray> {
        // need to replace spaces, but official java url encoding encodes too much for imgproxy
        val encodedUrl = url.replace(" ", "%20").plus("#${size ?: imageCachingConfig.width}")
        val cached = cache[encodedUrl]
        if (cached != null) {
            val mediaType = cached.contentType.getOrNull()
            return if (mediaType != null) {
                ResponseEntity.ok().contentType(mediaType).body(cached.optionalImage.getOrNull())
            } else {
                ResponseEntity.ok().body(cached.optionalImage.getOrNull())
            }
        }

        if (size != null) {
            require(imageCachingConfig.allowedSizes.contains(size))
        }

        val imgproxyUrl = signingService.createSignedImgProxyUrlForOperations(
            size ?: imageCachingConfig.width,
            size ?: imageCachingConfig.height,
            URI(encodedUrl)
        )

        try {
            val request = HttpRequest.newBuilder().GET()
                .uri(URI.create(imgproxyUrl))
                .build()
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())

            // TODO: maybe at some point we should switch to the restclient here as well
            if (response.statusCode() != 200) {
                logger.warn { "error requesting image: ${response.body().decodeToString()}" }
                return ResponseEntity.status(response.statusCode()).build()
            }

            var contentType: Optional<String> = Optional.empty()
            val optionalImage = if (response.statusCode() != 200) {
                logger.warn {
                    "response code is invalid ${response.statusCode()} for $imgproxyUrl\nreason: ${
                        response.body().decodeToString()
                    }"
                }
                return ResponseEntity.status(response.statusCode()).build()
            } else {
                val body = response.body()
                if (body.isEmpty()) {
                    logger.warn { "empty body for $imgproxyUrl" }
                    Optional.empty()
                } else {
                    contentType = response.headers().firstValue("content-type")
                    Optional.of(body)
                }
            }

            val optionalMediaType = contentType.map { MediaType.valueOf(it) }
            cache[encodedUrl] = CacheEntry(optionalImage, optionalMediaType)

            val mediaType = optionalMediaType.getOrNull()
            return if (mediaType != null) {
                ResponseEntity.ok().contentType(mediaType).body(optionalImage.getOrNull())
            } else {
                ResponseEntity.ok().body(optionalImage.getOrNull())
            }
        } catch (ex: Exception) {
            logger.warn {
                "exception when retrieving image: ${ex.message}"
            }
            return ResponseEntity.status(500).build()
        }
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    fun cleanUp() {
        val iterator = cache.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (isTooOld(entry.value.dateAdded)) {
                iterator.remove()
            }
        }
    }

    private fun isTooOld(dateAdded: Instant): Boolean {
        return dateAdded.isBefore(Instant.now().minus(1, ChronoUnit.DAYS))
    }

    data class CacheEntry(
        val optionalImage: Optional<ByteArray>,
        val contentType: Optional<MediaType>,
        val dateAdded: Instant = Instant.now(),
    )

    companion object {
        private val logger = KotlinLogging.logger { }
    }

}