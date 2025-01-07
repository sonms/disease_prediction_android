package com.example.diseasepredictionappproject.network.fast_api

import com.example.diseasepredictionappproject.BuildConfig
import com.example.diseasepredictionappproject.network.BaseUrlRetrofit
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

    private const val BASE_URL = BuildConfig.BASE_URL

    @Provides
    @Singleton
    @BaseUrlRetrofit // Qualifier 추가
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @BaseUrlRetrofit // Qualifier 추가
    fun provideApiService(@BaseUrlRetrofit retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}