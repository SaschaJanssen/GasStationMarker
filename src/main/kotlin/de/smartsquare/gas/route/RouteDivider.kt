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

    fun calcMarkers(directions: DirectionsResult, distance: Double): List<LatLng> {
        val route = getMajorMarkers(directions)

        val resultMarkers = ArrayList<LatLng>()

        var firstPoint = route[0]
        var distanceBetween = 0.0
        for ((index, value) in route.withIndex()) {

            if (index + 1 >= route.size) {
                break
            }

            var secondPoint = route[index + 1]

            var distanceBetween = distanceBetween.plus(distanceCalculcator.computeDistanceBetween(firstPoint, secondPoint))
            if (distanceBetween < distance) {
                continue;
            }

            val heading = distanceCalculcator.computeHeading(firstPoint, secondPoint)

            val newKmMarker = distanceCalculcator.computeOffset(firstPoint, distance, heading)

            firstPoint = secondPoint
            distanceBetween = 0.0
            resultMarkers.add(newKmMarker)
        }

        return resultMarkers

    }

}
