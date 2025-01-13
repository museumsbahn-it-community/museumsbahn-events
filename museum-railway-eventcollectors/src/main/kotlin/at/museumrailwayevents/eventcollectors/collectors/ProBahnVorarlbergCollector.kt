package at.museumrailwayevents.eventcollectors.collectors

import at.museumrailwayevents.eventcollectors.collectors.util.toTagsValue
import at.museumrailwayevents.model.conventions.*
import base.boudicca.SemanticKeys
import base.boudicca.model.Event
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Attach
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ProBahnVorarlbergCollector : MuseumRailwayEventCollector(
    operatorId = "pbv",
    locationId = "pbv",
    locationName = "PROBAHN Vorarlberg",
    sourceUrl = "https://probahn-vlbg.at/sonderfahrten/liste/",
) {
    override fun collectEvents(): List<Event> {
        val icsUrl = "$sourceUrl?ical=1"
        return parseEventFromIcs(URI(icsUrl).toURL())
    }

    private fun parseEventFromIcs(icsUrl: URL): List<Event> {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
        val daylongEventFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        icsUrl.openStream().use { inputStream ->
            val builder = CalendarBuilder()
            val calendar: Calendar = builder.build(inputStream)
            val components = calendar.components.filterIsInstance<VEvent>()

            return components.map { vEvent ->
                val eventName = vEvent.summary.value
                val eventStartDate = if (vEvent.isDaylongEvent()) {
                    LocalDate.parse(vEvent.startDate.value, daylongEventFormatter).atTime(0, 0)
                } else {
                    LocalDateTime.parse(vEvent.startDate.value, formatter)
                }.atZone(ZoneId.of("UTC")).toOffsetDateTime()
                val eventImageUrl = vEvent.properties.getProperty<Attach>(Property.ATTACH).uri

                createEvent(
                    eventName,
                    eventStartDate,
                    sourceUrl,
                    mutableMapOf
                        (
                        SemanticKeys.LOCATION_NAME to vEvent.location.value,
                        SemanticKeys.TAGS to TAGS_MUSEUM_RAILWAY_SPECIAL_TRIP.toTagsValue(),
                        SemanticKeys.DESCRIPTION to vEvent.description.value,
                        SemanticKeys.PICTURE_URL to eventImageUrl.toString(),
                        SemanticKeys.REGISTRATION to Registration.PRE_SALES_ONLY,
                        SemanticKeys.CATEGORY to Category.SPECIAL_TRIP,
                        CommonKeys.VEHICLE_TYPE to VehicleType.ELECTRIC_TRAIN, // PBV only running electric as well
                        SemanticKeys.RECURRENCE_TYPE to RecurrenceType.ONCE,
                        "url.ics" to icsUrl.toString(),
                        "pbv.uid" to vEvent.uid.value,
                        SemanticKeys.SOURCES to icsUrl.toString() + "\n" + sourceUrl
                    )
                )
            }
        }
    }

    private fun VEvent.isDaylongEvent(): Boolean {
        return this.startDate.toString().indexOf("VALUE=DATE") != -1
    }


    override fun getName(): String = "ProBahn Vorarlberg Collector"
}