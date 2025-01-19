package at.museumrailwayevents.model.conventions

import base.boudicca.*

// unfortunately these values have to be manually copied to javascript code
// remember this when editing any values
object CommonKeys {
    const val LOCATION_ID = "location_id"
    val LOCATION_ID_PROPERTY = TextProperty(LOCATION_ID)

    const val OPERATOR_ID = "operator_id"
    val OPERATOR_ID_PROPERTY = TextProperty(OPERATOR_ID)

    const val VEHICLE_TYPE = "locomotive_type"
    val VEHICLE_TYPE_PROPERTY = TextProperty(VEHICLE_TYPE)
}
