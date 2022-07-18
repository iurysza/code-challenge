package com.iurysouza.challenge.ui

import java.lang.Math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class DistanceCalculatorImpl : DistanceCalculator {
    /**
     * Calculate distance between two points in latitude and longitude.
     *  Uses Haversine method as its base.
     *
     * @returns Distance in Km
     */
    override fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val R = 6371 // Radius of the earth
        val latDistance = toRadians(lat2 - lat1)
        val lonDistance = toRadians(lon2 - lon1)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(toRadians(lat1)) * cos(toRadians(lat2))
                * sin(lonDistance / 2) * sin(lonDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        var distance = R * c * 1000 // convert to meters
        distance = distance.pow(2.0)
        val result = sqrt(distance)
        // convert result to kilometers
        return result / 1000
    }
}

interface DistanceCalculator {
    fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double
}
