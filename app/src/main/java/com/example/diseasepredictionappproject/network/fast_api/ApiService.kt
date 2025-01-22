package com.example.diseasepredictionappproject.network.fast_api

import com.example.diseasepredictionappproject.data.PillInfoDataRequest
import com.example.diseasepredictionappproject.data.PillInfoDataResponse
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.data.PredictionImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("predict")
    fun getPrediction(): Call<Void>

    @GET("features-with-high-importance")
    fun getImportantFeatures() : Call<PredictionFeaturesData>

    @POST("predict-disease")
    fun postPredictDisease(@Body predictionFeatures : PredictionFeaturesData) : Call<PredictionDiseaseResponse>

    @Multipart
    @POST("pill-predict")
    fun postPredictPill(
        @Part file: MultipartBody.Part
    ): Call<PredictionImageResponse>


    @POST("pill-info")
    fun postPillName(
        @Body pillName : PillInfoDataRequest
    ) : Call<PillInfoDataResponse>
}