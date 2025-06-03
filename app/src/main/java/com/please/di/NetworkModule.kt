package com.please.di

import android.util.Log
import com.please.data.api.AuthApiService
import com.please.data.api.DriverApiService
import com.please.data.api.GoogleMapApi
import com.please.data.api.PathAiDeliveryApi
import com.please.data.api.PathAiPickupApi
import com.please.data.api.SellerProfileApi
import com.please.data.api.SubscriptionApi
import com.please.data.api.TspApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

//현재 elb로 다이렉트 접근. api gateway스킵
//const val BASE_URL = "https://2y3az5fho1.execute-api.ap-northeast-2.amazonaws.com/Subtrack-stage/"
//const val BASE_URL = "http://43.200.131.230:3000/"
//const val  BASE_URL = "http://web-alb-subtrack-462963304.ap-northeast-2.elb.amazonaws.com/"
const val BASE_URL = "http://elb-subtrack-alb-535559136.ap-northeast-2.elb.amazonaws.com/"
const val BASE_URL_GEOMERTY = "https://maps.googleapis.com/"
const val BASE_URL_CHAT_AI = "https://api.example.com/"
const val BASE_URL_TSP = "http://elb-subtrack-alb-535559136.ap-northeast-2.elb.amazonaws.com:8080/"
const val BASE_URL_PATH_AI = "http://elb-subtrack-alb-535559136.ap-northeast-2.elb.amazonaws.com/api/"
const val BASE_URL_PATH_RAW_AI = "http://elb-subtrack-alb-535559136.ap-northeast-2.elb.amazonaws.com/api/"

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
    @Named("default")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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
    fun provideDriverApiService(@Named("default") retrofit: Retrofit): DriverApiService {
        return retrofit.create(DriverApiService::class.java)
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
    fun providePathAiApi(@Named("path") retrofit: Retrofit): PathAiDeliveryApi {
        return retrofit.create(PathAiDeliveryApi::class.java)
    }

    @Provides
    @Singleton
    @Named("path_raw")
    fun providePathAiRawRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_PATH_RAW_AI)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePathAiRawApi(@Named("path_raw") retrofit: Retrofit): PathAiPickupApi {
        return retrofit.create(PathAiPickupApi::class.java)
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

    // ===== TSP API 추가 =====
    @Provides
    @Singleton
    @Named("tsp")
    fun provideTspRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_TSP)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTspApiService(@Named("tsp") retrofit: Retrofit): TspApiService {
        return retrofit.create(TspApiService::class.java)
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