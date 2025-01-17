package com.example.diseasepredictionappproject.data

import com.google.gson.annotations.SerializedName

data class PredictionImageResponse(
    @SerializedName("prediction_pill_name")
    val predictionPillName: String
)
