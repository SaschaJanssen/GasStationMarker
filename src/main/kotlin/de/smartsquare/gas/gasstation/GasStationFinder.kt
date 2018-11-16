package de.smartsquare.gas.gasstation

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.maps.model.LatLng
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class GasStationFinder {

    fun findCheapestGasStation(markers: List<LatLng>, radius: Double): Events {
        val chunks: Flowable<Triple<Request, Response, Result<GasStations, FuelError>>> =
            Flowable.fromArray(markers)
                .flatMapIterable { it -> it }
                .delay((500..5000).random().toLong(), TimeUnit.MILLISECONDS)
                .map { "https://creativecommons.tankerkoenig.de/json/list.php" to parametersOf(it, radius) }
                .map { it -> it.first.httpGet(it.second) }
                .map { it -> it.responseObject(GasStations.Deserializer()) }
                .buffer(10)
                .flatMapIterable { it -> it }

        return Events(
            success = chunks
                .filter { it -> it.third is Result.Success }
                .map { it -> it.third.component1() }
                .filter { it -> it.ok }
                .map { it -> it.stations }
                .flatMapIterable { it -> it }
                .sorted(compareBy(GasStations.GasStation::diesel))
                .takeLast(1),
            error = chunks.filter { it -> it.third is Result.Failure }
                .map { it -> it.second }
        )
    }

    private fun parametersOf(marker: LatLng, radius: Double): List<Pair<String, String>> {
        return listOf(
            "lat" to "${marker.lat}",
            "lng" to "${marker.lng}",
            "rad" to "$radius",
            "sort" to "dist",
            "type" to "all",
            "apikey" to "b8bba741-6fd7-1d31-7229-d38691f67194"
        )
    }

}