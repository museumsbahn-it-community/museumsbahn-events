package at.museumrailwayevents.service

import at.museumrailwayevents.config.ImageCachingConfig
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class ImgproxyUrlSigningService(private val imageCachingConfig: ImageCachingConfig) {

    fun createSignedImgProxyUrl(originalUrl: String): String {
        val key = hexStringToByteArray("943b421c9eb07c830af81030552c86009268de4e532ba2ee2eab8247c6da0881")
        val salt = hexStringToByteArray("520f986b998545b4785e0defbc4f3c1203f22de2374a3d53cb7a7fe9fea309c5")
        val path = "/rt:fit/mw:${imageCachingConfig.width}/mh:${imageCachingConfig.height}/plain/${originalUrl}"
        val pathWithHash = signPath(key, salt, path)

        return "${imageCachingConfig.imgProxyUrl}$pathWithHash"
    }

    companion object {
        private fun signPath(key: ByteArray?, salt: ByteArray?, path: String): String {
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
            val res = ByteArray(hex.length / 2)
            for (i in res.indices) {
                res[i] = (((hex[i * 2].digitToIntOrNull(16) ?: (-1 shl 4)) or (hex[i * 2 + 1].digitToIntOrNull(16)
                        ?: -1))).toByte()
            }
            return res
        }
    }
}