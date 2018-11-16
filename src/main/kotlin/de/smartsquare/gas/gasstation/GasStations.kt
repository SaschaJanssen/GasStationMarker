package de.smartsquare.gas.gasstation

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class GasStations(val ok: Boolean, val stations: List<GasStation>) {
    data class GasStation(
        val name: String,
        val lat: Double,
        val lng: Double,
        val dist: Double,
        val diesel: Double,
        val e5: Double,
        val e10: Double,
        val isOpen: Boolean
    )

    class Deserializer : ResponseDeserializable<GasStations> {
        override fun deserialize(content: String): GasStations = Gson().fromJson(content, GasStations::class.java)
    }
}