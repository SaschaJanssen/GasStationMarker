package de.sq.gas

import java.lang.Math.*

class MMathUtil {
    /**
     * Restrict x to the range [low, high].
     */
    fun clamp(x: Double, low: Double, high: Double): Double {
        return if (x < low) low else if (x > high) high else x
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
     * Returns mercator Y corresponding to latitude.
     * See http://en.wikipedia.org/wiki/Mercator_projection .
     */
    fun mercator(lat: Double): Double {
        return log(tan(lat * 0.5 + PI / 4))
    }

    /**
     * Returns latitude from mercator Y.
     */
    fun inverseMercator(y: Double): Double {
        return 2 * atan(exp(y)) - PI / 2
    }

    /**
     * Returns haversine(angle-in-radians).
     * hav(x) == (1 - cos(x)) / 2 == sin(x / 2)^2.
     */
    fun hav(x: Double): Double {
        val sinHalf = sin(x * 0.5)
        return sinHalf * sinHalf
    }

    /**
     * Computes inverse haversine. Has good numerical stability around 0.
     * arcHav(x) == acos(1 - 2 * x) == 2 * asin(sqrt(x)).
     * The argument must be in [0, 1], and the result is positive.
     */
    fun arcHav(x: Double): Double {
        return 2 * asin(sqrt(x))
    }

    // Given h==hav(x), returns sin(abs(x)).
    fun sinFromHav(h: Double): Double {
        return 2 * sqrt(h * (1 - h))
    }

    // Returns hav(asin(x)).
    fun havFromSin(x: Double): Double {
        val x2 = x * x
        return x2 / (1 + sqrt(1 - x2)) * .5
    }

    // Returns sin(arcHav(x) + arcHav(y)).
    fun sinSumFromHav(x: Double, y: Double): Double {
        val a = sqrt(x * (1 - x))
        val b = sqrt(y * (1 - y))
        return 2 * (a + b - 2 * (a * y + b * x))
    }

    /**
     * Returns hav() of distance from (lat1, lng1) to (lat2, lng2) on the unit sphere.
     */
    fun havDistance(lat1: Double, lat2: Double, dLng: Double): Double {
        return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2)
    }
}
