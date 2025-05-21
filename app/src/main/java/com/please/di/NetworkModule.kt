package com.please.di

import com.please.data.api.AuthApiService
import com.please.data.api.GoogleMapApi
import com.please.data.api.SellerProfileApi
import com.please.data.api.SubscriptionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

//현재 elb로 다이렉트 접근. api gateway스킵
//const val BASE_URL = "https://2y3az5fho1.execute-api.ap-northeast-2.amazonaws.com/Subtrack-stage/"
//const val BASE_URL = "http://43.200.131.230:3000/"
//const val  BASE_URL = "http://web-alb-subtrack-462963304.ap-northeast-2.elb.amazonaws.com/"
const val BASE_URL = "https://vw0y369jm5.execute-api.ap-northeast-2.amazonaws.com/Subtrack/"
const val BASE_URL_GEOMERTY = "https://api.example.com/"
const val BASE_URL_PATH_AI = "https://api.example.com/"
const val BASE_URL_CHAT_AI = "https://api.example.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("default")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // 임시 URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("default") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSubscriptionApi(@Named("default") retrofit: Retrofit): SubscriptionApi {
        return retrofit.create(SubscriptionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSellerProfileApi(@Named("default") retrofit: Retrofit): SellerProfileApi {
        return retrofit.create(SellerProfileApi::class.java)
    }

    @Provides
    @Singleton
    @Named("geometry")
    fun provideGeometryRetrofit(): Retrofit {
        return Retrofit.Builder()
        .baseUrl(BASE_URL_GEOMERTY)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    }

    @Provides
    @Singleton
    fun provideGoogleMapApi(@Named("geometry") retrofit: Retrofit): GoogleMapApi {
        return retrofit.create(GoogleMapApi::class.java)
    }

    @Provides
    @Singleton
    @Named("path")
    fun providePathAiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_PATH_AI)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("chat")
    fun provideChatAiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_CHAT_AI)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /*
    val authService: AuthService by lazy {
        provideAuthRetrofit().create(AuthService::class.java)
    }

    val dataService: DataService by lazy {
        provideDataRetrofit().create(DataService::class.java)
    }


     */

}