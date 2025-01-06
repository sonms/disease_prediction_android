package com.example.diseasepredictionappproject.view_model.repository

import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.network.ApiService
import retrofit2.Call
import javax.inject.Inject

class RetrofitFastApiRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun postPredictionFeatures(features: PredictionFeaturesData): Call<PredictionDiseaseResponse> {
        return apiService.postPredictDisease(features)
    }

    fun getImportantFeatures(): Call<PredictionFeaturesData> {
        return apiService.getImportantFeatures()
    }
}