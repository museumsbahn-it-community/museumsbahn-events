package at.museumsbahnen

import at.museumsbahnen.model.Location
import at.museumsbahnen.model.MuseumLocation
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import java.io.File

@Path("/api/location")
class LocationsResource {

    companion object {
        private val LOCATION_INDEX = 5;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun listLocations(): List<MuseumLocation> {
        val classLoader = javaClass.getClassLoader()
        val resource = classLoader.getResource("locations.csv")
        val reader = File(resource.file).bufferedReader()
        reader.readLine() // skip header
        val museums = reader.lineSequence()
            .filter { it.isNotBlank() }.map { line ->
                val data = line.split(",")

                val location = Location(
                    lat = data[LOCATION_INDEX].trim().toFloat(),
                    lon = data[LOCATION_INDEX + 1].trim().toFloat(),
                    country = "Österreich",
                    state = data[LOCATION_INDEX + 2],
                    street = data[LOCATION_INDEX + 3],
                    city = data[LOCATION_INDEX + 4],
                    zipCode = data[LOCATION_INDEX + 5],
                )

                MuseumLocation(
                    name = data[0],
                    shortName = data[1],
                    location,
                    webUrl = data[2],
                    "",
                    ""
                )
            }.toList()

        return museums
    }
}
