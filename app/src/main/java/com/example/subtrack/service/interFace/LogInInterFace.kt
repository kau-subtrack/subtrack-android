package com.example.subtrack.service.interFace
import com.example.subtrack.data.model.reqUserLogIn
import com.example.subtrack.data.model.reqUserLogIns
import com.example.subtrack.data.model.resUserToken
import com.example.subtrack.data.model.resUserTokens
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LogInInterFace {
    @POST("auth/login")
    fun logIn(@Body user:reqUserLogIn): Call<resUserToken>
}