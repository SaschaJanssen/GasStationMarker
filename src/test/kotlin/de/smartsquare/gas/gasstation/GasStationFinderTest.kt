package de.smartsquare.gas.gasstation

import com.google.maps.model.LatLng
import org.junit.jupiter.api.Test

class GasStationFinderTest {

    private val smartsquare = LatLng(52.00522000, 8.56053000)
    private val venne = LatLng(52.387653, 8.1663592)

    private val gasStationFinder = GasStationFinder()

    @Test
    fun find() {
        val events = gasStationFinder.findCheapestGasStation(markers = listOf(smartsquare, venne, venne, venne, venne, venne), radius = 20.0)

        events.success.subscribe { println(it) }
        events.error.subscribe { println(it) }

        Thread.sleep(50000)
    }

}