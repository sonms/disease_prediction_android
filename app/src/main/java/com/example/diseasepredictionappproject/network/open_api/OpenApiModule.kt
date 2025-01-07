package com.example.diseasepredictionappproject.network.open_api

import com.example.diseasepredictionappproject.BuildConfig
import com.example.diseasepredictionappproject.network.GovUrlRetrofit
import com.example.diseasepredictionappproject.network.fast_api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OpenApiModule {

    private const val GOV_URL = BuildConfig.GOV_URL

    @Provides
    @Singleton
    @GovUrlRetrofit // Qualifier 추가
    fun provideOpenApiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOV_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @GovUrlRetrofit // Qualifier 추가
    fun provideOpenApiService(@GovUrlRetrofit retrofit: Retrofit): OpenApiService {
        return retrofit.create(OpenApiService::class.java)
    }
}