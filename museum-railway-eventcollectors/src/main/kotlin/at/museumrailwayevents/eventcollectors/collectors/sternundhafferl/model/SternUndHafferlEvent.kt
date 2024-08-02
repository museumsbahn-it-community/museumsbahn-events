package at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.model

import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.serializers.LocalDateSerializer
import at.museumrailwayevents.eventcollectors.collectors.sternundhafferl.serializers.LocalTimeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.apache.commons.text.StringEscapeUtils
import java.time.LocalDate
import java.time.LocalTime

//{
//    "id":158,
//    "title":"Kinderspa\u00df auf der Atterseebahn",
//    "url":"https:\/\/www.stern-verkehr.at\/ausfluege-events\/kinderspass\/",
//    "is_event":true,
//    "subtitle":"Abenteuer mit Bahn & Schiff am Attersee",
//    "show_registerForm":false,
//    "location":"Attersee",
//    "startdate":"2024-07-09",
//    "enddate":"2024-09-03",
//    "all_day_event":false,
//    "recurring_event":true,
//    "recurring_information":[
//      {
//          "type":"weekday",
//          "weekday":{"days":["2"]},
//          "fixed_dates":{"dates":false},
//          "interval":{
//              "repeatDuration":"",
//              "durationType":false
//          }
//       }
//    ],
//    "fromTime":"10:00",
//    "toTime":"12:40",
//    "featured_image_url":"https:\/\/www.stern-verkehr.at\/wp-content\/uploads\/2023\/10\/2024-02-01-D-JN-Individualprogramm-Sujets6.jpg"
//}

//{
//    "id":5336,
//    "title":"Mondscheinbummel in Gmunden",
//    "url":"https:\/\/www.stern-verkehr.at\/ausfluege-events\/monscheinbummel\/",
//    "is_event":true,
//    "show_registerForm":false,
//    "subtitle":"",
//    "location":"",
//    "startdate":"2024-08-01",
//    "enddate":"2024-08-01",
//    "all_day_event":false,
//    "fromTime":"16:00",
//    "toTime":"00:00",
//    "recurring_event":false,
//    "featured_image_url":"https:\/\/www.stern-verkehr.at\/wp-content\/uploads\/2024\/05\/2024-SM-Sujets-Verkehr-Mondschein-Bummel-3-1024x1024.jpg"
//}

// TODO: handle recurring event information

val sternUndHafferlJsonModule = SerializersModule {
    polymorphic(RecurringInformation::class) {
        subclass(RecurringInformationWeekdays::class)
        subclass(RecurringInformationFixedDates::class)
    }
}

@Serializable
data class SternUndHafferlEvent(
    val id: Int,
    private val title: String,
    val url: String,
    val is_event: Boolean,
    val show_registerForm: Boolean,
    val subtitle: String,
    val location: String,
    val recurring_information: List<RecurringInformation>? = null,
    @Serializable(with = LocalDateSerializer::class)
    val startdate: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val enddate: LocalDate? = null,
    val all_day_event: Boolean,
    @Serializable(with = LocalTimeSerializer::class)
    val fromTime: LocalTime? = null,
    @Serializable(with = LocalTimeSerializer::class)
    val toTime: LocalTime? = null,
    val recurring_event: Boolean,
//    @JsonNames("featured_image_url")
    val featured_image_url: String
) {
    val parsedTitle: String = StringEscapeUtils.unescapeHtml4(title)
    val parsedSubtitle: String = StringEscapeUtils.unescapeHtml4(subtitle)
}

@Serializable
abstract class RecurringInformation

@Serializable
@SerialName("weekday")
data class RecurringInformationWeekdays(
    val weekday: Weekdays
): RecurringInformation()

@Serializable
data class Weekdays(
    val days: List<String>
)

@Serializable
@SerialName("fixed_dates")
data class RecurringInformationFixedDates @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("fixed_dates")
    val fixedDates: FixedDates
): RecurringInformation()

@Serializable
data class FixedDates(
    val dates: List<SimpleDate>
)

@Serializable
data class SimpleDate (
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
)

// TODO: in the testdata an interval never occurred, so we gotta implement it when it's there
@Serializable
@SerialName("???")
data class RecurringInterval(
    val repeatDuration: String,
    val durationType: Boolean
)