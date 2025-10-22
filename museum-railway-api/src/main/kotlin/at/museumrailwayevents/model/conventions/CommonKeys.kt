package at.museumrailwayevents.model.conventions

import base.boudicca.*

// unfortunately these values have to be manually copied to javascript code
// remember this when editing any values
object CommonKeys {
    const val LOCATION_ID = "location_id"
    val LOCATION_ID_PROPERTY = TextProperty(LOCATION_ID)

    const val OPERATOR_ID = "operator_id"
    val OPERATOR_ID_PROPERTY = TextProperty(OPERATOR_ID)

    const val VEHICLE_TYPE = "vehicle_type"
    val VEHICLE_TYPE_PROPERTY = TextProperty(VEHICLE_TYPE)

    const val MUSEUM_EVENT_REGISTRATION = "museum_event_registration"
    val MUSEUM_EVENT_REGISTRATION_PROPERTY = TextProperty(MUSEUM_EVENT_REGISTRATION)

    const val OPERATION_TYPE = "operation_type"
    val OPERATION_TYPE_PROPERTY = TextProperty(OPERATION_TYPE)

    const val MUSEUM_EVENTS_CATEGORY = "museum_events_category"
    val MUSEUM_EVENTS_CATEGORY_PROPERTY = TextProperty(MUSEUM_EVENTS_CATEGORY)
}
