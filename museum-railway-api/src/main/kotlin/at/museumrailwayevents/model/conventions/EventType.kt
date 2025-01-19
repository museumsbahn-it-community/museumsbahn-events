package at.museumrailwayevents.model.conventions

// unfortunately these values have to be manually copied to javascript code
// remember this when editing any values
enum class EventType(jsonValue: String) {
    SPECIAL_TRIP("specialTrip"),
    MUSEUM_OPENING("museumOpening"),
    MUSEUM_EVENT("museumEvent"),
    MODELRAILWAY_OPENING("modelRailwayOpening"),
}