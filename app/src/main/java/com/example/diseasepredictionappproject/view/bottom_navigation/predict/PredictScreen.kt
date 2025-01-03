package com.example.diseasepredictionappproject.view.bottom_navigation.predict

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diseasepredictionappproject.data.HealthTipData
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.loading.LoadingState
import com.example.diseasepredictionappproject.network.RetrofitClient
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

    var predictionDiseaseName by remember {
        mutableStateOf("")
    }

    var postData by remember { mutableStateOf(mutableListOf<Int>()) }


    val context = LocalContext.current

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
                        postData.add(1)
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
                        postData.add(0)
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
            val data = PredictionFeaturesData(
                itching = postData.getOrNull(0) ?: 0,
                joint_pain = postData.getOrNull(1) ?: 0,
                stomach_pain = postData.getOrNull(2) ?: 0,
                vomiting = postData.getOrNull(3) ?: 0,
                fatigue = postData.getOrNull(4) ?: 0,
                high_fever = postData.getOrNull(5) ?: 0,
                dark_urine = postData.getOrNull(6) ?: 0,
                nausea = postData.getOrNull(7) ?: 0,
                loss_of_appetite = postData.getOrNull(8) ?: 0,
                abdominal_pain = postData.getOrNull(9) ?: 0,
                diarrhoea = postData.getOrNull(10) ?: 0,
                mild_fever = postData.getOrNull(11) ?: 0,
                yellowing_of_eyes = postData.getOrNull(12) ?: 0,
                chest_pain = postData.getOrNull(13) ?: 0,
                muscle_weakness = postData.getOrNull(14) ?: 0,
                muscle_pain = postData.getOrNull(15) ?: 0,
                altered_sensorium = postData.getOrNull(16) ?: 0,
                family_history = postData.getOrNull(17) ?: 0,
                mucoid_sputum = postData.getOrNull(18) ?: 0,
                lack_of_concentration = postData.getOrNull(19) ?: 0
            )

            Log.d("prediction 데이터", data.toString())

            postPredictionFeatures(data, context) {
                it?.let {
                    predictionDiseaseName = it
                }
            }

            // 결과 출력
            Text(
                text = "예측된 질병: $predictionDiseaseName",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

fun postPredictionFeatures(
    features: PredictionFeaturesData?,
    context: Context,
    onResult: (String?) -> Unit
) {
    LoadingState.show()
    if (features != null) {
        RetrofitClient.instance.postPredictDisease(features).enqueue(object :
            Callback<PredictionDiseaseResponse> {
            override fun onResponse(
                call: Call<PredictionDiseaseResponse>,
                response: Response<PredictionDiseaseResponse>
            ) {
                if (response.isSuccessful) {
                    val diseaseName = response.body()?.diseaseName
                    Log.d("issuccess", diseaseName.toString())
                    onResult(diseaseName) // 결과값 전달
                } else {
                    Log.e("postP", response.message().toString())
                    Toast.makeText(context, "예측에 실패하였습니다. ${response.code()}", Toast.LENGTH_SHORT)
                        .show()
                    onResult(null) // 에러 시 null 전달
                }
            }

            override fun onFailure(call: Call<PredictionDiseaseResponse>, t: Throwable) {
                Log.e("postP", t.message.toString())
                onResult(null) // 에러 시 null 전달
            }
        })
    }
}