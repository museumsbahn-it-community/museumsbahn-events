package at.museumrailwayevents.api

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@RequestMapping("/imgcache")
interface ImageCachingProxyApi {
    @GetMapping(
        produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE]
    )
    @ResponseBody
    fun getImage(@RequestParam url: String): ResponseEntity<ByteArray>
}