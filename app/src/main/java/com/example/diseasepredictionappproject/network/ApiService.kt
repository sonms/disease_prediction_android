package com.example.diseasepredictionappproject.network

import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("predict")
    fun getPrediction(): Call<Void>

    @GET("/features-with-high-importance")
    fun getImportantFeatures() : Call<PredictionFeaturesData>

    @POST("/predict-disease")
    fun postPredictDisease(@Body predictionFeatures : PredictionFeaturesData) : Call<PredictionDiseaseResponse>
}