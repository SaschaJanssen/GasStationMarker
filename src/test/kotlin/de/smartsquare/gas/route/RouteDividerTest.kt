package de.smartsquare.gas.route

import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.AndroidSphericalCalculator
import com.google.maps.model.LatLng
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

class RouteDividerTest {

    private val routeDivider = RouteDivider(AndroidSphericalCalculator())

    private val smartsquare = LatLng(52.00522000, 8.56053000)
    private val home = LatLng(52.001250000000006,8.56287)

    @Test
    fun should_calculate_waypoints_between_two_addresses() {
        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyADz7Tc0xusnydQiQzNWsx5kc-pxTpKWBE")
            .build()
        val origin = "Otto-Brenner-Str, 247, 33604 Bielefeld, Germany"
        val destination = "Lipper Hellweg, 84, 33605 Bielefeld, Germany"
        val directions = DirectionsApi.getDirections(context, origin, destination).await()

        val waypointsOnRoute = routeDivider.getMarkers(directions, distance = 200.0)

        waypointsOnRoute.size shouldEqual 43
        waypointsOnRoute shouldContain smartsquare
        waypointsOnRoute shouldContain home
    }

}
