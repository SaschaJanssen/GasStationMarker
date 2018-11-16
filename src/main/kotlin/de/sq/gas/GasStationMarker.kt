package de.sq.gas

import com.google.maps.DirectionsApi.getDirections
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import java.util.ArrayList


fun main(args: Array<String>) {

    val context = GeoApiContext.Builder()
            .apiKey("AIzaSyADz7Tc0xusnydQiQzNWsx5kc-pxTpKWBE")
            .build()


    val origin = "Avinguda Diagonal, 101, 08005 Barcelona, Spain"
    val destination = "Carrer de París, 67, 08029 Barcelona, Spain"


    val directions = getDirections(context, origin, destination)
    val res = directions.await()

    GasStationMarker().getWaypointsOnRoute(res)
}

class GasStationMarker {


    fun getWaypointsOnRoute(directions: DirectionsResult): List<LatLng> = directions.routes
            .map { it -> it.legs }
            .reduce { first, second -> first + second }
            .map { it.steps }
            .reduce { first, second -> first + second }
            .map { it -> it.polyline }
            .map { it -> it.decodePath() }
            .reduce { first, second -> first + second }
            .toList()


    fun getMarkersEveryNMeters(path: List<LatLng>, distance: Double): List<LatLng> {
        val res = ArrayList<LatLng>()

        val p0 = path[0]
        res.add(p0)
        if (path.size > 2) {
            //Initialize temp variables for sum distance between points and
            //and save the previous point
            var tmp = 0.0
            var prev = p0
            for (p in path) {
                //Sum the distance
                tmp += SphericalUtil().computeDistanceBetween(prev, p)
                if (tmp < distance) {
                    //If it is less than certain value continue sum
                    prev = p
                    continue
                } else {
                    //If distance is greater than certain value lets calculate
                    //how many meters over desired value we have and find position of point
                    //that will be at exact distance value
                    val diff = tmp - distance
                    val heading = SphericalUtil().computeHeading(prev, p)

                    val pp = SphericalUtil().computeOffsetOrigin(p, diff, heading)

                    //Reset sum set calculated origin as last point and add it to list
                    tmp = 0.0
                    prev = pp!!
                    res.add(pp!!)
                    continue
                }
            }

            //Add the last point of route
            val plast = path[path.size - 1]
            res.add(plast)
        }

        return res
    }
}
