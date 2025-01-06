package com.example.diseasepredictionappproject.data

import com.google.gson.annotations.SerializedName

data class PredictionDiseaseResponse (
    @SerializedName("predicted_disease")
    val diseaseName: String
)