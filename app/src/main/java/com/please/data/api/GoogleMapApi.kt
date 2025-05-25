package com.please.data.api

import com.please.data.models.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapApi {
    @GET("maps/api/geocode/json")
    suspend fun getLatLngFromAddress(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}
