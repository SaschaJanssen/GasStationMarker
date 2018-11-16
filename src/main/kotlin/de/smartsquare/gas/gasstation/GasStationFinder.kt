package de.smartsquare.gas.gasstation

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.rx.rx_responseObject
import com.github.kittinunf.result.Result
import com.google.maps.model.LatLng
import io.reactivex.Maybe
import io.reactivex.Observable

class GasStationFinder {

    fun findCheapestGasStation(markers: List<LatLng>): Maybe<GasStations.GasStation> {
        val requests: List<Observable<Pair<Response, Result<GasStations, FuelError>>>> = markers
            .map { "https://creativecommons.tankerkoenig.de/json/list.php" to parametersOf(it) }
            .map { it -> it.first.httpGet(it.second) }
            .map { it -> it.rx_responseObject(GasStations.Deserializer()).toObservable() }

        return Observable.merge(requests)
            .filter { it -> it.second is Result.Success }
            .map { it -> it.second.component1()?.stations }
            .flatMapIterable { it -> it }
            .distinct()
            .sorted(compareBy(GasStations.GasStation::diesel))
            .firstElement()
    }

    private fun parametersOf(it: LatLng): List<Pair<String, String>> {
        return listOf(
            "lat" to "${it.lat}",
            "lng" to "${it.lng}",
            "rad" to "1",
            "sort" to "dist",
            "type" to "all",
            "apikey" to "CHANGEME"
        )
    }

}