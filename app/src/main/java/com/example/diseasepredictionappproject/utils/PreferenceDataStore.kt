package com.example.diseasepredictionappproject.utils

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class FontSize(val size: Float) {
    data object Small : FontSize(12f)
    data object Medium : FontSize(16f)
    data object Large : FontSize(20f)
}

private val Context.dataStore by preferencesDataStore(name = "disease_settings")

object PreferenceDataStore {
    private val FONT_SIZE_KEY = stringPreferencesKey("font_size")

    // 글자 크기 Flow
    fun getFontSizeFlow(context: Context): Flow<FontSize> = context.dataStore.data
        .map { preferences ->
            when (preferences[FONT_SIZE_KEY]) {
                "SMALL" -> FontSize.Small
                "MEDIUM" -> FontSize.Medium
                "LARGE" -> FontSize.Large
                else -> FontSize.Medium // 기본 값
            }
        }

    // 글자 크기 저장
    suspend fun setFontSize(context: Context, fontSize: FontSize) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = when (fontSize) {
                FontSize.Small -> "SMALL"
                FontSize.Medium -> "MEDIUM"
                FontSize.Large -> "LARGE"
            }
        }
    }
}