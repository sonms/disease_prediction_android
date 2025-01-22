package com.example.diseasepredictionappproject.data

import com.google.gson.annotations.SerializedName

data class PillInfoDataResponse(
    @SerializedName("Business_Name")
    val businessName : String,
    @SerializedName("Classification_Name")
    val classificationName : String,
    @SerializedName("Open_Date")
    val openDate : String
)
