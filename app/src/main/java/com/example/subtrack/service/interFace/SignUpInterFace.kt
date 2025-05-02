package com.example.subtrack.service.interFace
import com.example.subtrack.data.model.resUserRegister
import com.example.subtrack.data.model.reqUserSignUp
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpInterFace {
    @POST("auth/signup") // @Method(api address)
    fun signUp(@Body user:reqUserSignUp): Call<resUserRegister>
}