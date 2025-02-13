package com.example.diseasepredictionappproject.utils

import android.content.Context

class RequestCodeManager(context: Context) {
    private val prefs = context.getSharedPreferences("requestCodePrefs", Context.MODE_PRIVATE)

    fun getRequestCode(): Int {
        return prefs.getInt("requestCode", 1000)
    }

    private fun saveRequestCode(requestCode: Int) {
        prefs.edit().putInt("requestCode", requestCode).apply()
    }

    fun getNextRequestCode(): Int {
        val nextCode = getRequestCode() + 1
        saveRequestCode(nextCode)
        return nextCode
    }
}
