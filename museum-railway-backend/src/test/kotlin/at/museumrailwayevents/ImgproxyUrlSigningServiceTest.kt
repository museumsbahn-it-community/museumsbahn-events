package at.museumrailwayevents

import assertk.assertThat
import assertk.assertions.isEqualTo
import at.museumrailwayevents.config.ImageCachingConfig
import at.museumrailwayevents.service.ImgproxyUrlSigningService
import org.junit.jupiter.api.Test


class ImgproxyUrlSigningServiceTest {

    @Test
    fun testWithJavaHmacApacheBase64ImgProxyTest() {
        val key = "943b421c9eb07c830af81030552c86009268de4e532ba2ee2eab8247c6da0881"
        val salt = "520f986b998545b4785e0defbc4f3c1203f22de2374a3d53cb7a7fe9fea309c5"
        val config = ImageCachingConfig().also {
            it.imgProxyUrl = ""
            it.signingKey = key
            it.signingSalt = salt
        }

        val service = ImgproxyUrlSigningService(config)

        val path = "/rs:fit:300:300/plain/http://img.example.com/pretty/image.jpg"
        val pathWithHash: String = service.createSignedImgProxyUrl(path)
        assertThat(pathWithHash).isEqualTo("/m3k5QADfcKPDj-SDI2AIogZbC3FlAXszuwhtWXYqavc/rs:fit:300:300/plain/http://img.example.com/pretty/image.jpg")
    }
}