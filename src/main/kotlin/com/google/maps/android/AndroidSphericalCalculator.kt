package com.google.maps.android

import com.google.maps.model.LatLng
import de.smartsquare.gas.route.DistanceCalculator
import de.smartsquare.gas.route.RouteDivider

class AndroidSphericalCalculator : DistanceCalculator {

    /**
     * Returns the angle between two LatLngs, in radians. This is the same as the distance
     * on the unit sphere.
     */
    private fun computeAngleBetween(from: LatLng, to: LatLng): Double {
        return distanceRadians(
            Math.toRadians(from.lat), Math.toRadians(from.lng),
            Math.toRadians(to.lat), Math.toRadians(to.lng)
        )
    }

    /**
     * Returns the distance between two LatLngs, in meters.
     */
    override fun computeDistanceBetween(from: LatLng, to: LatLng): Double {
        return computeAngleBetween(from, to) * RouteDivider.EARTH_RADIUS
    }

    /**
     * Returns distance on the unit sphere; the arguments are in radians.
     */
    private fun distanceRadians(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2))
    }

    /**
     * Returns the heading from one LatLng to another LatLng. Headings are
     * expressed in degrees clockwise from North within the range [-180,180).
     * @return The heading in degrees clockwise from north.
     */
    override fun computeHeading(from: LatLng, to: LatLng): Double {
        val fromLat = Math.toRadians(from.lat)
        val fromLng = Math.toRadians(from.lng)
        val toLat = Math.toRadians(to.lat)
        val toLng = Math.toRadians(to.lng)
        val dLng = toLng - fromLng
        val heading = Math.atan2(
            Math.sin(dLng) * Math.cos(toLat),
            Math.cos(fromLat) * Math.sin(toLat) - Math.sin(fromLat) * Math.cos(toLat) * Math.cos(dLng)
        )
        return wrap(Math.toDegrees(heading), -180.0, 180.0)
    }

    /**
     * Returns the LatLng resulting from moving a distance from an origin
     * in the specified heading (expressed in degrees clockwise from north).
     * @param from     The LatLng from which to start.
     * @param distance The distance to travel.
     * @param heading  The heading in degrees clockwise from north.
     */
    override fun computeOffset(from: LatLng, distance: Double, heading: Double): LatLng {
        var distance = distance
        var heading = heading
        distance /= RouteDivider.EARTH_RADIUS
        heading = Math.toRadians(heading)
        val fromLat = Math.toRadians(from.lat)
        val fromLng = Math.toRadians(from.lng)
        val cosDistance = Math.cos(distance)
        val sinDistance = Math.sin(distance)
        val sinFromLat = Math.sin(fromLat)
        val cosFromLat = Math.cos(fromLat)
        val sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * Math.cos(heading)
        val dLng = Math.atan2(
            sinDistance * cosFromLat * Math.sin(heading),
            cosDistance - sinFromLat * sinLat
        )
        return LatLng(Math.toDegrees(Math.asin(sinLat)), Math.toDegrees(fromLng + dLng))
    }

    /**
     * Wraps the given value into the inclusive-exclusive interval between min and max.
     * @param n   The value to wrap.
     * @param min The minimum.
     * @param max The maximum.
     */
    fun wrap(n: Double, min: Double, max: Double): Double {
        return if (n >= min && n < max) n else mod(n - min, max - min) + min
    }

    /**
     * Returns the non-negative remainder of x / m.
     * @param x The operand.
     * @param m The modulus.
     */
    fun mod(x: Double, m: Double): Double {
        return (x % m + m) % m
    }

    /**
     * Returns haversine(angle-in-radians).
     * hav(x) == (1 - cos(x)) / 2 == sin(x / 2)^2.
     */
    fun hav(x: Double): Double {
        val sinHalf = Math.sin(x * 0.5)
        return sinHalf * sinHalf
    }

    /**
     * Computes inverse haversine. Has good numerical stability around 0.
     * arcHav(x) == acos(1 - 2 * x) == 2 * asin(sqrt(x)).
     * The argument must be in [0, 1], and the result is positive.
     */
    fun arcHav(x: Double): Double {
        return 2 * Math.asin(Math.sqrt(x))
    }

    /**
     * Returns hav() of distance from (lat1, lng1) to (lat2, lng2) on the unit sphere.
     */
    fun havDistance(lat1: Double, lat2: Double, dLng: Double): Double {
        return hav(lat1 - lat2) + hav(dLng) * Math.cos(lat1) * Math.cos(lat2)
    }

}