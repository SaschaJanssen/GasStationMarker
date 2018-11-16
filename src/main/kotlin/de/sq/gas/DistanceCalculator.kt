package de.sq.gas

import com.google.maps.model.LatLng

interface DistanceCalculator {
∆
    fun computeDistanceBetween(from: LatLng, to: LatLng): Double

    fun computeHeading(from: LatLng, to: LatLng): Double

    fun computeOffset(from: LatLng, distance: Double, heading: Double): LatLng

}