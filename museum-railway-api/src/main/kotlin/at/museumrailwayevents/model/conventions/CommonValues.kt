package at.museumrailwayevents.model.conventions

// unfortunately these values have to be manually copied to javascript code
// remember this when editing any values
object Tags {
    const val HISTORIC_TRAIN = "historic_train"
    const val HISTORY = "history"
    const val NARROW_GAUGE = "narrow_gauge"
    const val MUSEUM = "museum"
    const val MUSEUM_TRAIN = "museum_train"
    val MUSEUM_RAILWAY = MuseumEventsCategory.MUSEUM_RAILWAY.jsonValue
    val RAILWAY_MUSEUM = MuseumEventsCategory.RAILWAY_MUSEUM.jsonValue
    val MUSEUM_EVENT = MuseumEventsCategory.MUSEUM_EVENT.jsonValue // a special event in a museum, like a concert
    const val HISTORIC_TRAIN_TRIP = "historic_train_trip" // only for trips on regular rail network
    const val HISTORIC_SHIP_TRIP = "historic_ship_trip"
}

object VehicleType {
    const val DIESEL_TRAIN = "diesel_train"
    const val STEAM_TRAIN = "steam_train"
    const val ELECTRIC_TRAIN = "electric_train"
    const val TRAM = "tram"
    const val SHIP = "ship"
}

enum class MuseumEventRegistration(val jsonValue: String) {
    FREE("free"), // free
    REGISTRATION("registration"), // free, but requires registration
    PRE_SALES_ONLY("pre-sales-only"),
    RESERVATION_RECOMMENDED("reservation-recommended"),
    PRIVATE_EVENT("private-event"), // not in use at the moment, but could be useful for events like photo trains
    TICKET("ticket"),
}

@Deprecated("use boudicca recurrence type instead")
object RecurrenceType {
    const val REGULARLY = "regularly"
    const val RARELY = "rarely"
    const val ONCE = "once"
}

/**
 * categories of events
 */
enum class MuseumEventsCategory(val jsonValue: String) {
    SPECIAL_TRIP("special_trip"), // Sonderfahrt - excursion on public rails or a special event on the museum railway
    RAILWAY_MUSEUM("railway_museum"), // Museum - opening day of a museum, for museums without regular opening days or special events
    MUSEUM_RAILWAY("museum_railway"), // Museumsbahn - running day of a dedicated museum railway
    MUSEUM_EVENT("museum_event"), // Veranstaltung - besondere Veranstaltung, Konzert etc.
    MODEL_RAILWAY("model_railway"), // Modellbahn - not in use at the moment
}

val TAGS_MUSEUM_EVENT =
    setOf(Tags.HISTORY, Tags.MUSEUM, Tags.MUSEUM_EVENT, Tags.RAILWAY_MUSEUM)
val TAGS_MUSEUM_RAILWAY_SPECIAL_TRIP =
    setOf(Tags.HISTORY, Tags.HISTORIC_TRAIN, Tags.MUSEUM_TRAIN, Tags.HISTORIC_TRAIN_TRIP)
val TAGS_MUSEUM_RAILWAY_OPERATING = setOf(Tags.HISTORY, Tags.HISTORIC_TRAIN, Tags.MUSEUM_TRAIN, Tags.MUSEUM_RAILWAY)
val TAGS_NARROW_GAUGE =
    setOf(Tags.HISTORY, Tags.HISTORIC_TRAIN, Tags.MUSEUM_TRAIN, Tags.MUSEUM_RAILWAY, Tags.NARROW_GAUGE)