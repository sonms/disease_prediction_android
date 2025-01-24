package com.example.diseasepredictionappproject.utils

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object FontUtils {
    fun getTextStyle(fontSize: FontSize): TextStyle {
        return TextStyle(
            fontSize = fontSize.size.sp,
            fontWeight = FontWeight.Normal
        )
    }
}