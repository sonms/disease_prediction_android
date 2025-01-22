package com.example.diseasepredictionappproject.view_model.repository

import android.content.Context
import android.net.Uri
import com.example.diseasepredictionappproject.data.PillInfoDataRequest
import com.example.diseasepredictionappproject.data.PillInfoDataResponse
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.data.PredictionImageResponse
import com.example.diseasepredictionappproject.network.BaseUrlRetrofit
import com.example.diseasepredictionappproject.network.fast_api.ApiService
import com.example.diseasepredictionappproject.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import javax.inject.Inject

class RetrofitFastApiRepository @Inject constructor(
    @BaseUrlRetrofit
    private val apiService: ApiService,
) {
    fun postPredictionFeatures(features: PredictionFeaturesData): Call<PredictionDiseaseResponse> {
        return apiService.postPredictDisease(features)
    }

    fun getImportantFeatures(): Call<PredictionFeaturesData> {
        return apiService.getImportantFeatures()
    }

    fun postImagePredictionPill(imageUri: Uri, context: Context): Call<PredictionImageResponse> {
        //uri를 file로 생성
        val file = FileUtils.getFileFromUri(context, imageUri)
        //file을 request body로 변환
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        //multipart 생성
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return apiService.postPredictPill(body)
    }

    fun postPillNameInfo(pillName : PillInfoDataRequest) : Call<PillInfoDataResponse> {
        return apiService.postPillName(pillName)
    }
}