package de.smartsquare.gas.route

import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng

class RouteDivider(private val distanceCalculcator: DistanceCalculator) {

    companion object {
        const val EARTH_RADIUS = 6371009.0
    }

    fun getMarkers(directions: DirectionsResult, distance: Double): List<LatLng> {
        val majorMarkers = getMajorMarkers(directions)
        val intermediateMarkers = getIntermediateMarkers(majorMarkers, distance)

        return (intermediateMarkers)
    }

    private fun getMajorMarkers(directions: DirectionsResult): List<LatLng> = directions.routes
            .map { it -> it.legs }
            .reduce { current, next -> current + next }
            .map { it.steps }
            .reduce { current, next -> current + next }
            .map { it -> it.polyline }
            .map { it -> it.decodePath() }
            .reduce { current, next -> current + next }
            .toList()

    private fun getIntermediateMarkers(route: List<LatLng>, distance: Double): List<LatLng> = route.zipWithNext()
            .filter { it -> distanceCalculcator.computeDistanceBetween(it.first, it.second) > distance }
            .map { it ->
                val heading = distanceCalculcator.computeHeading(it.first, it.second)
                val currentDistance = distanceCalculcator.computeDistanceBetween(it.first, it.second)
                val markersNeeded = (distance / currentDistance).toInt()
                val ranges = 1..markersNeeded
                ranges.map { i -> distanceCalculcator.computeOffset(it.first, i * distance, heading) }
            }
            .flatten()

    fun calcMarkers(directions: DirectionsResult, minimumDistance: Double): List<LatLng> {
        val route = getMajorMarkers(directions)

        return when {
            route.isEmpty() -> emptyList()
            else -> route.drop(1)
                    .fold(listOf(route.first())) { current, next ->
                        val distance = distanceCalculcator.computeDistanceBetween(current.last(), next)

                        when (distance >= minimumDistance) {
                            true -> current + next
                            false -> current
                        }
                    }
                    .zipWithNext()
                    .map { (firstPoint, secondPoint) ->
                        val heading = distanceCalculcator.computeHeading(firstPoint, secondPoint)

                        distanceCalculcator.computeOffset(firstPoint, minimumDistance, heading)
                    }
        }
    }

}
