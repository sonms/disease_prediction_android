package com.example.diseasepredictionappproject.view_model.repository

import com.example.diseasepredictionappproject.data.DrugInfoRequest
import com.example.diseasepredictionappproject.data.DrugInfoResponse
import com.example.diseasepredictionappproject.network.GovUrlRetrofit
import com.example.diseasepredictionappproject.network.open_api.OpenApiService
import retrofit2.Call
import javax.inject.Inject

class OpenApiRepository @Inject constructor(
    @GovUrlRetrofit
    private val openApiService: OpenApiService
) {
    fun getDrugInfoData(
        serviceKey: String,
        pageNo: Int? = 1,
        numOfRows: Int? = 3,
        entpName: String? = null,
        itemName: String? = null,
        itemSeq: String? = null,
        efcyQesitm: String? = null,
        useMethodQesitm: String? = null,
        atpnWarnQesitm: String? = null,
        atpnQesitm: String? = null,
        intrcQesitm: String? = null,
        seQesitm: String? = null,
        depositMethodQesitm: String? = null,
        openDe: String? = null,
        updateDe: String? = null,
        type: String? = "json"
    ): Call<DrugInfoResponse> {
        return openApiService.getDrbEasyDrugList(
            serviceKey = serviceKey,
            pageNo = pageNo,
            numOfRows = numOfRows,
            entpName = entpName,
            itemName = itemName,
            itemSeq = itemSeq,
            efcyQesitm = efcyQesitm,
            useMethodQesitm = useMethodQesitm,
            atpnWarnQesitm = atpnWarnQesitm,
            atpnQesitm = atpnQesitm,
            intrcQesitm = intrcQesitm,
            seQesitm = seQesitm,
            depositMethodQesitm = depositMethodQesitm,
            openDe = openDe,
            updateDe = updateDe,
            type = type
        )
    }

    /*
    * fun postPredictionFeatures(features: PredictionFeaturesData): Call<PredictionDiseaseResponse> {
        return apiService.postPredictDisease(features)
    }*/
}