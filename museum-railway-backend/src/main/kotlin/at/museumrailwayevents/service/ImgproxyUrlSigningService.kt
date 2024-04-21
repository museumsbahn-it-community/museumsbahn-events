package at.museumrailwayevents.service

import at.museumrailwayevents.config.ImageCachingConfig
import org.apache.tomcat.util.buf.UriUtil
import org.springframework.stereotype.Service
import org.springframework.web.util.UriUtils
import java.net.URI
import java.nio.charset.Charset
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class ImgproxyUrlSigningService(private val imageCachingConfig: ImageCachingConfig) {

    fun createSignedImgProxyUrlForOperations(width: Int, height: Int, originalUrl: URI): String {
        val encodedOriginalUrl = UriUtils.encode(originalUrl.toString(), Charset.forName("UTF-8"))
        val path = "/rt:fit/mw:${width}/mh:${height}/plain/${encodedOriginalUrl}"
        println(path) // TODO: remove
        return createSignedImgProxyUrl(path)
    }

    fun createSignedImgProxyUrl(path: String): String {
        val key = hexStringToByteArray(imageCachingConfig.signingKey)
        val salt = hexStringToByteArray(imageCachingConfig.signingSalt)
        val pathWithHash = signPath(key, salt, path)

        return "${imageCachingConfig.imgProxyUrl}$pathWithHash"
    }

    companion object {
        fun signPath(key: ByteArray?, salt: ByteArray?, path: String): String {
            val hmacsha256 = "HmacSHA256"
            val sha256HMAC = Mac.getInstance(hmacsha256)
            val secretKey = SecretKeySpec(key, hmacsha256)
            sha256HMAC.init(secretKey)
            sha256HMAC.update(salt)
            val hash: String = Base64.getUrlEncoder().withoutPadding().encodeToString(sha256HMAC.doFinal(path.toByteArray()))
            return "/$hash$path"
        }

        private fun hexStringToByteArray(hex: String): ByteArray {
            require(hex.length % 2 == 0) { "Even-length string required" }
            return hex.chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        }
    }
}