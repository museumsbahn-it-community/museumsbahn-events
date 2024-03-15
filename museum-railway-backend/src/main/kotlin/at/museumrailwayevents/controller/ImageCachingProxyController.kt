package at.museumrailwayevents.controller

import at.museumrailwayevents.api.ImageCachingProxyApi
import at.museumrailwayevents.config.ImageCachingConfig
import at.museumrailwayevents.service.ImgproxyUrlSigningService
import org.slf4j.LoggerFactory
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
import kotlin.math.max

@RestController
class ImageCachingProxyController(private val imageCachingConfig: ImageCachingConfig,
        private val signingService: ImgproxyUrlSigningService) : ImageCachingProxyApi {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    private val cache = ConcurrentHashMap<String, CacheEntry>()

    private val httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()

    override fun getImage(url: String): Optional<ByteArray> {
        val cached = cache[url]
        if (cached != null) {
            return cached.optionalPicture
        }

        val imgproxyUrl = signingService.createSignedImgProxyUrl(url)

        val request = HttpRequest.newBuilder().GET()
                .uri(URI.create(imgproxyUrl))
                .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())

        val optional = if (response.statusCode() != 200) {
            LOG.warn("response code is invalid ${response.statusCode()} for $imgproxyUrl")
            Optional.empty()
        } else {
            val body = response.body()
            if (body.isEmpty()) {
                LOG.warn("empty body for $imgproxyUrl")
                Optional.empty()
            } else {
                Optional.of(body)
            }
        }

        cache[url] = CacheEntry(optional)

        return optional
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
            val optionalPicture: Optional<ByteArray>,
            val dateAdded: Instant = Instant.now(),
    )

}