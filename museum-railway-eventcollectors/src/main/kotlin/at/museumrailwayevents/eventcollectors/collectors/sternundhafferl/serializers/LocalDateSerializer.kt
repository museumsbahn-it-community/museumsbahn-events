package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
object LocalDateSerializer {
    override fun deserialize(decoder: Decoder): LocalDate {
        val dateString = decoder.decodeString()
        return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        val dateString = value.format(DateTimeFormatter.ISO_DATE)
        encoder.encodeString(dateString)
    }

}