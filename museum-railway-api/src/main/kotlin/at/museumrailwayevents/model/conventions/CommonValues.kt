package at.museumrailwayevents.model.conventions

// unfortunately these values have to be manually copied to javascript code
// remember this when editing any values
object Tags {
    const val LOCOMOTIVE_TYPE_DIESEL = "diesel"
    const val LOCOMOTIVE_TYPE_STEAM = "steam"
    const val LOCOMOTIVE_TYPE_ELECTRIC = "electric"
    const val NOSTALGIC_EVENT = "nostalgic_event"
    const val HISTORIC_TRAIN = "historic_train"
    const val HISTORY = "history"
    const val NARROW_GAUGE = "narrow_gauge"
    const val MUSEUM_TRAIN = "museum_train"
    const val MUSEUM_RAILWAY = "museum_railway" // only for trains running on dedicated museum railway tracks
    const val MUSEUM = "museum"
    const val RAILWAY_MUSEUM = "railway_museum"
    const val MUSEUM_EVENT = "museum_event" // a special event in a museum, like a concert
    const val MODEL_RAILWAY = "model_railway"
    const val HISTORIC_TRAIN_TRIP = "historic_train_trip" // only for trips on regular rail network
    const val HISTORIC_SHIP_TRIP = "historic_ship_trip"
}

object Registration {
    const val FREE = "free"
    const val REGISTRATION = "registration"
    const val PRE_SALES_ONLY = "pre-sales-only"
    const val TICKET = "ticket"
}

object RecurrenceType {
    const val REGULARLY = "regularly"
    const val RARELY = "rarely"
    const val ONCE = "once"
}

const val CATEGORY_MUSEUM_TRAIN = "MUSEUM_TRAIN"
const val CATEGORY_RAILWAY_MUSEUM = "RAILWAY_MUSEUM"

val TAGS_MUSEUM_EVENT =
    listOf(Tags.HISTORY, Tags.MUSEUM, Tags.MUSEUM_EVENT, Tags.RAILWAY_MUSEUM)
val TAGS_MUSEUM_RAILWAY_SPECIAL_TRIP =
    listOf(Tags.HISTORY, Tags.HISTORIC_TRAIN, Tags.MUSEUM_TRAIN, Tags.HISTORIC_TRAIN_TRIP)
val TAGS_MUSEUM_RAILWAY_OPERATING = listOf(Tags.HISTORY, Tags.HISTORIC_TRAIN, Tags.MUSEUM_TRAIN, Tags.MUSEUM_RAILWAY)
val TAGS_NARROW_GAUGE =
    listOf(Tags.HISTORY, Tags.HISTORIC_TRAIN, Tags.MUSEUM_TRAIN, Tags.MUSEUM_RAILWAY, Tags.NARROW_GAUGE)