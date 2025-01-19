package at.museumrailwayevents.controller

import at.museumrailwayevents.api.ImageCachingProxyApi
import at.museumrailwayevents.config.ImageCachingConfig
import at.museumrailwayevents.service.ImgproxyUrlSigningService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull

@Controller
class ImageCachingProxyController(
    private val imageCachingConfig: ImageCachingConfig,
    private val signingService: ImgproxyUrlSigningService,
) : ImageCachingProxyApi {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    private val cache = ConcurrentHashMap<String, CacheEntry>()

    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    override fun getImage(url: String): ResponseEntity<ByteArray> {
        // need to replace spaces, but official java url encoding encodes too much for imgproxy
        val encodedUrl = url.replace(" ", "%20")
        val cached = cache[encodedUrl]
        if (cached != null) {
            val mediaType = cached.contentType.getOrNull()
            return if (mediaType != null) {
                ResponseEntity.ok().contentType(mediaType).body(cached.optionalImage.getOrNull())
            } else {
                ResponseEntity.ok().body(cached.optionalImage.getOrNull())
            }
        }

        val imgproxyUrl = signingService.createSignedImgProxyUrlForOperations(
            imageCachingConfig.width,
            imageCachingConfig.height,
            URI(encodedUrl)
        )

        val request = HttpRequest.newBuilder().GET()
            .uri(URI.create(imgproxyUrl))
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())

        var contentType: Optional<String> = Optional.empty()
        val optionalImage = if (response.statusCode() != 200) {
            LOG.warn("response code is invalid ${response.statusCode()} for $imgproxyUrl\nreason: ${response.body().decodeToString()}")
            return ResponseEntity.status(response.statusCode()).build()
        } else {
            val body = response.body()
            if (body.isEmpty()) {
                LOG.warn("empty body for $imgproxyUrl")
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

}