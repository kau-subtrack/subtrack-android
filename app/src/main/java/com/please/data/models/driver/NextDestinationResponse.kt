package com.please.data.models.driver

import com.google.gson.annotations.SerializedName

data class NextDestinationResponse(
    val status: String,
    val message: String? = null,
    @SerializedName("next_destination")
    val nextDestination: NextDestination? = null,
    val route: RouteResponse? = null,
    @SerializedName("remaining_pickups")
    val remainingPickups: Int = 0,
    @SerializedName("is_last")
    val isLast: Boolean = false
)

data class NextDestination(
    val lat: Double,
    val lon: Double,
    @SerializedName("parcel_id")
    val parcelId: String,
    val name: String,
    val address: String
)

data class RouteResponse(
    val coordinates: List<Coordinate>? = null,
    val waypoints: List<Waypoint>? = null,
    val trip: RouteTrip? = null
)

data class Coordinate(
    val lat: Double,
    val lon: Double
)

data class Waypoint(
    val lat: Double,
    val lon: Double,
    val name: String,
    val instruction: String
)

data class RouteTrip(
    val summary: RouteSummary? = null
)

data class RouteSummary(
    val time: Double = 0.0,
    val length: Double = 0.0
)