package de.smartsquare.gas.gasstation

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.rx.rx_responseObject
import com.github.kittinunf.result.Result
import com.google.maps.model.LatLng
import io.reactivex.Maybe

class GasStationFinder {

    fun findAllStationsInRadius(start: LatLng, radius: Double): Maybe<List<GasStations.GasStation>> =
        "https://creativecommons.tankerkoenig.de/json/list.php"
            .httpGet(parametersOf(start))
            .rx_responseObject(GasStations.Deserializer())
            .toMaybe()
            .filter { it -> it.second is Result.Success }
            .map { it -> it.second.component1()?.stations }


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
