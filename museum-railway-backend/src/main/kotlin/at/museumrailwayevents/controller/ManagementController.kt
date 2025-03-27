package at.museumrailwayevents.controller

import at.museumrailwayevents.service.GoogleDataLoaderService
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

private const val RATE_LIMIT_SECONDS = 30L

@RequestMapping("/api/manage")
@RestController
class ManagementController(private val dataLoaderService: GoogleDataLoaderService) {

    private var lastReloadTime = Instant.now()

    @PostMapping("/reloadData")
    fun clearCaches(): ResponseEntity<String> {
        // rate limit to maximum of 1 request per minute
        if (Duration.between(lastReloadTime, Instant.now()).toSeconds() < RATE_LIMIT_SECONDS) {
            return ResponseEntity
                .status(HttpStatusCode.valueOf(429))
                .body("next request possible at ${LocalDateTime.ofInstant(lastReloadTime.plusSeconds(RATE_LIMIT_SECONDS), UTC)}")
        }

        dataLoaderService.reloadData()
        lastReloadTime = Instant.now()
        return ResponseEntity.ok().body("data updated")
    }
}