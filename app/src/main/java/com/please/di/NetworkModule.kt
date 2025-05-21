package com.please.di

import android.util.Log
import com.please.data.api.AuthApiService
import com.please.data.api.DriverApiService
import com.please.data.api.SellerProfileApi
import com.please.data.api.SubscriptionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//현재 elb로 다이렉트 접근. api gateway스킵
//const val BASE_URL = "https://2y3az5fho1.execute-api.ap-northeast-2.amazonaws.com/Subtrack-stage/"
//const val BASE_URL = "http://43.200.131.230:3000/"
//const val  BASE_URL = "http://web-alb-subtrack-462963304.ap-northeast-2.elb.amazonaws.com/"
const val BASE_URL = "https://vw0y369jm5.execute-api.ap-northeast-2.amazonaws.com/Subtrack/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("API_CALL", message)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideSubscriptionApi(retrofit: Retrofit): SubscriptionApi {
        return retrofit.create(SubscriptionApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideSellerProfileApi(retrofit: Retrofit): SellerProfileApi {
        return retrofit.create(SellerProfileApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideDriverApiService(retrofit: Retrofit): DriverApiService {
        return retrofit.create(DriverApiService::class.java)
    }
}
