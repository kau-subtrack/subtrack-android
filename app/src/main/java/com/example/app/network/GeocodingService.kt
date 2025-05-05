package com.example.app.network

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("maps/api/geocode/json")
    suspend fun getLatLngFromAddress(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}

data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val formatted_address: String,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
