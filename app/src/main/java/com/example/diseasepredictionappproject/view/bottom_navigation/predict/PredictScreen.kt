package com.example.diseasepredictionappproject.view.bottom_navigation.predict

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diseasepredictionappproject.data.HealthTipData
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.view_model.PredictionViewModel


fun getPredictionFeatures(): List<PredictionDiseaseResponse> {
    return listOf(
        PredictionDiseaseResponse(
            "가려움증"
        ),
        PredictionDiseaseResponse(
            "관절 통증"
        ),
        PredictionDiseaseResponse(
            "복통"
        ),
        PredictionDiseaseResponse(
            "구토"
        ),
        PredictionDiseaseResponse(
            "피로"
        ),
        PredictionDiseaseResponse(
            "고열"
        ),
        PredictionDiseaseResponse(
            "어두운 소변"
        ),
        PredictionDiseaseResponse(
            "메스꺼움"
        ),
        PredictionDiseaseResponse(
            "식욕 상실"
        ),
        PredictionDiseaseResponse(
            "복부 통증"
        ),
        PredictionDiseaseResponse(
            "설사"
        ),
        PredictionDiseaseResponse(
            "약한 발열"
        ),
        PredictionDiseaseResponse(
            "눈이 노랗게 변함"
        ),
        PredictionDiseaseResponse(
            "가슴 통증"
        ),
        PredictionDiseaseResponse(
            "근육 약화"
        ),
        PredictionDiseaseResponse(
            "근육통"
        ),
        PredictionDiseaseResponse(
            "변경된 감각"
        ),
        PredictionDiseaseResponse(
            "가족력"
        ),
        PredictionDiseaseResponse(
            "점액가래"
        ),
        PredictionDiseaseResponse(
            "집중력 부족"
        ),
    )
}

@Composable
fun PredictScreen(
    viewModel: PredictionViewModel = hiltViewModel()
) {
    var isClick by remember {
        mutableStateOf(false)
    }

    val getFeaturesName = getPredictionFeatures()

    var listIndex by remember {
        mutableStateOf(0)
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //질문 시작하기 묻기
        if (!isClick) {
            Text(text = "스스로 질병 예측하기")

            Button(
                modifier = Modifier.padding(start = 20.dp),
                onClick = {
                    isClick = !isClick
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = blueColor4
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "시작")
            }
        }


        if (isClick && listIndex < 20) {
            Text(
                text = "${getFeaturesName[listIndex].diseaseName} 증상이 있으신가요?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                //o
                Button(
                    onClick = { 
                        listIndex += 1
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "O",
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            color = Color.Blue
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(0.3f))

                //x
                Button(
                    onClick = {
                        listIndex += 1
                    }
                ) {
                    Text(
                        text = "X",
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            color = Color.Red
                        )
                    )
                }
            }
        }
        
        if (listIndex == 20) {
            Text(text = "2202020202020200202임")
        }
    }
}