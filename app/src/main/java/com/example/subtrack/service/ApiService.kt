package com.example.subtrack.service

import com.example.subtrack.data.model.HomeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("api/owner/home")
    suspend fun getHomeInfo(
        @Header("Authorization") token: String
    ): Response<HomeResponse>
}
