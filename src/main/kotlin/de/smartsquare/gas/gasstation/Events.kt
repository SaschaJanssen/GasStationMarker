package de.smartsquare.gas.gasstation

import com.github.kittinunf.fuel.core.Response
import io.reactivex.Flowable

data class Events(val success: Flowable<GasStations.GasStation>, val error: Flowable<Response>)