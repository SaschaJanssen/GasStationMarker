package de.smartsquare.gas

import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng

class GasStationMarker(private val distanceCalculcator: DistanceCalculator) {

    companion object {
        const val EARTH_RADIUS = 6371009.0
    }

    fun getMarkers(directions: DirectionsResult, distance: Double): List<LatLng> {
        val majorMarkers = getMajorMarkers(directions)
        val intermideateMarkers = getIntermidiateMarkers(majorMarkers, distance)

        return majorMarkers + intermideateMarkers
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

    private fun getIntermidiateMarkers(route: List<LatLng>, distance: Double): List<LatLng> = route.zipWithNext()
        .filter { it -> distanceCalculcator.computeDistanceBetween(it.first, it.second) > distance }
        .map { it ->
            val heading = distanceCalculcator.computeHeading(it.first, it.second)
            val currentDistance = distanceCalculcator.computeDistanceBetween(it.first, it.second)
            val markersNeeded = (currentDistance / distance).toInt()
            val ranges = 1..markersNeeded
            ranges.map { i -> distanceCalculcator.computeOffset(it.first, i * distance, heading) }
        }
        .flatten()

}
