package de.smartsquare.gas

import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.AndroidSphericalCalculator
import de.smartsquare.gas.gasstation.GasStationFinder
import de.smartsquare.gas.route.RouteDivider
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.text.DateFormat


fun main(args: Array<String>) {

    val gasStationMarker = RouteDivider(AndroidSphericalCalculator())
    val gasStationFinder = GasStationFinder()

    val context = GeoApiContext.Builder()
            .apiKey("AIzaSyADz7Tc0xusnydQiQzNWsx5kc-pxTpKWBE")
            .build()

    val server = embeddedServer(Netty, port = 8080) {

        install(DefaultHeaders)
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            method(HttpMethod.Patch)
            header(HttpHeaders.Authorization)
            allowCredentials = true
            anyHost()
        }
        install(Compression)
        install(CallLogging)
        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
                setPrettyPrinting()
            }
        }

        routing {
            get("/") {

                val origin = call.parameters["origin"]
                val destination = call.parameters["destination"]

                val directions = DirectionsApi.getDirections(context, origin, destination).await()

                val markers = gasStationMarker.getMarkers(directions, 200.0)
                // val cheapestGasStation = gasStationFinder.findAllStationsInRadius(markers)
                //      call.respond(cheapestGasStation.blockingGet())

                //val twentyKmRadiusCoordinates = gasStationMarker.getMarkerEachNKm(directions, 20000.0)

                //val cheapestGasStation = gasStationFinder.findCheapestGasStation(markers)
                //call.respond(cheapestGasStation.blockingGet())
            }

            get("/allmarker") {
                val origin = call.parameters["origin"]
                val destination = call.parameters["destination"]

                val directions = DirectionsApi.getDirections(context, origin, destination).await()

                //val twentyKmRadiusCoordinates = gasStationMarker.getMarkerEachNKm(directions, 200.0)
                val markers = gasStationMarker.getMarkers(directions, 200.0)

                call.respond(markers)
            }
        }

    }
    server.start(wait = true)
}
