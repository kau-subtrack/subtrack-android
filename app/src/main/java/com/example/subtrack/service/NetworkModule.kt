package com.example.subtrack.service
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//현재 elb로 다이렉트 접근. api gateway스킵
//const val BASE_URL = "https://2y3az5fho1.execute-api.ap-northeast-2.amazonaws.com/Subtrack-stage/"
//const val BASE_URL = "http://43.200.131.230:3000/"
const val  BASE_URL = "https://vw0y369jm5.execute-api.ap-northeast-2.amazonaws.com/Subtrack/"

fun getRetrofit(): Retrofit {
    val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    return retrofit
}