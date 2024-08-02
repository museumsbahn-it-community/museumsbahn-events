package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalTime::class)
object LocalTimeSerializer {
    override fun deserialize(decoder: Decoder): LocalTime {
        val dateString = decoder.decodeString()
        return LocalTime.parse(dateString, DateTimeFormatter.ISO_TIME)
    }

    override fun serialize(encoder: Encoder, value: LocalTime) {
        val dateString = value.format(DateTimeFormatter.ISO_TIME)
        encoder.encodeString(dateString)
    }

}