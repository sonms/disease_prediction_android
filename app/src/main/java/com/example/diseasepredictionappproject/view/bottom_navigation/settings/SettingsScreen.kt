package com.example.diseasepredictionappproject.view.bottom_navigation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController
) {
    //언어 설정?

    //다크모드

    //문의 및 설문조사?

    //글자 크기 조정?
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)




    Column {
        Text(text = "글자 크기 조정", style = FontUtils.getTextStyle(fontSize.size))

        Row {
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    PreferenceDataStore.setFontSize(context, FontSize.Small)
                }
            }) {
                Text("작게")
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    PreferenceDataStore.setFontSize(context, FontSize.Medium)
                }
            }) {
                Text("중간")
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    PreferenceDataStore.setFontSize(context, FontSize.Medium)
                }
            }) {
                Text("크게")
            }
        }
    }
}