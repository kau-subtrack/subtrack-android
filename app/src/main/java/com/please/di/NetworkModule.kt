package com.please.di

import com.please.data.api.AuthApiService
import com.please.data.api.SellerProfileApi
import com.please.data.api.SubscriptionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.please.com/") // 임시 URL
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
}
