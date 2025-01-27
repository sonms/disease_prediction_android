package com.example.diseasepredictionappproject.view.bottom_navigation.predict

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.diseasepredictionappproject.R
import com.example.diseasepredictionappproject.data.PredictionDiseaseResponse
import com.example.diseasepredictionappproject.data.PredictionFeaturesData
import com.example.diseasepredictionappproject.loading.LoadingState
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.ui.theme.blueColor5
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import com.example.diseasepredictionappproject.view_model.FastApiViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PredictScreen(
    viewModel: PredictionViewModel = hiltViewModel(),
    fastApiViewModel: FastApiViewModel = hiltViewModel()
) {
    val featureNames = listOf(
        "가려움", "관절 통증", "구토", "피로", "고열",
        "발한", "짙은 소변", "메스꺼움", "식욕 부진", "복부 통증",
        "설사", "미열", "눈의 황변", "가슴 통증", "비틀거림",
        "근육통", "감각 이상", "몸에 붉은 반점", "가족력", "집중력 부족"
    )
    val uiState by fastApiViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)

    //질문제어
    var isClick by remember { //시작 시 버튼 클릭 remember
        mutableStateOf(false)
    }
    var listIndex by remember {
        mutableStateOf(0)
    }


    //피처 값들 제어
    val getFeaturesName = getPredictionFeatures()

    var predictionDiseaseName by remember {
        mutableStateOf<String?>(null)
    }

    val postData by remember { mutableStateOf(mutableListOf<Int>()) }

    var features by remember {
        mutableStateOf<PredictionFeaturesData?>(null)
    }

    //디자인 제어
    var visible by remember {
        mutableStateOf(false)
    }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1.0f else 0f, label = ""
    )
    var isSaved by remember { mutableStateOf(false) }

    //통신제어
    var isPredictionComplete by remember { mutableStateOf(false) }

    //초기화
    LaunchedEffect(Unit) {
        predictionDiseaseName = null
        // 예측 완료 후 isPredictionComplete 상태 변경
        isPredictionComplete = false
        visible = false
        isSaved = false
        features = null
        listIndex = 0
        isClick = false

        LoadingState.hide()
    }

    // 처음 한 번만 API 호출이 이루어지도록 설정
    if (!isPredictionComplete) {
        LaunchedEffect(features) {
            features?.let {
                fastApiViewModel.fetchPrediction(it)
            }
        }
    }

    if (predictionDiseaseName != null) {
        LaunchedEffect(predictionDiseaseName) {
            isPredictionComplete = !isPredictionComplete
            visible = !visible
            Log.d("predictionDiseaseName", predictionDiseaseName.toString())
            Log.d("isPredictionComplete", isPredictionComplete.toString())
        }
    }

    when (uiState) {
        is FastApiViewModel.UiState.Loading -> {
            CoroutineScope(Dispatchers.IO).launch {
                LoadingState.show()
                try {
                    // 비동기 작업 수행
                } finally {
                    LoadingState.hide()
                }
            }
        }

        is FastApiViewModel.UiState.DiseasePrediction -> {
            try {
                val diseaseName = (uiState as FastApiViewModel.UiState.DiseasePrediction).data
                Log.d("issuccess", diseaseName)
                // diseaseName을 UI에 반영하거나, 필요한 작업을 추가로 수행
                predictionDiseaseName = diseaseName
                // 예측 완료 후 isPredictionComplete 상태 변경
                isPredictionComplete = true
                fastApiViewModel.fetchUIState(FastApiViewModel.UiState.Wait)
                LoadingState.hide()
            } catch (e: Exception) {
                fastApiViewModel.fetchUIState(FastApiViewModel.UiState.Wait)
            }
        }

        is FastApiViewModel.UiState.Error -> {
            val error = (uiState as FastApiViewModel.UiState.Error).message
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }

        is FastApiViewModel.UiState.Wait -> {

        }

        else -> {

        }
    }

    // API 호출 결과를 기억
    val featuresName = remember { mutableStateOf(emptyList<PredictionDiseaseResponse>()) }
    LaunchedEffect(Unit) {
        featuresName.value = getPredictionFeatures()
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
            Text(
                text = "스스로 질병 예측하기",
                style = FontUtils.getTextStyle(fontSize.size + 4f)
            )

            Button(
                modifier = Modifier.padding(top = 20.dp),
                onClick = {
                    isClick = !isClick
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = blueColor4
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "시작",
                    style = FontUtils.getTextStyle(fontSize.size)
                )
            }
        }


        if (isClick && listIndex < 20) {
            Text(
                text = "${getFeaturesName[listIndex].diseaseName} 증상이 있으신가요?",
                style = FontUtils.getTextStyle(fontSize.size + 8f),
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
                        style = FontUtils.getTextStyle(fontSize.size),
                        color = Color.Blue
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
                        style = FontUtils.getTextStyle(fontSize.size),
                        color = Color.Red
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

            features = data
        }


        if (isPredictionComplete) {
            // 결과 출력
            Text(
                text = "예측된 질병: $predictionDiseaseName",
                style = FontUtils.getTextStyle(fontSize.size),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .alpha(animatedAlpha)
            )


            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp)
                    .alpha(animatedAlpha),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "저장하시겠습니까?",
                    style = FontUtils.getTextStyle(fontSize.size),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 16.dp)
                )

                IconButton(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = {
                    val activeFeatures = featureNames.zip(postData).filter { it.second == 1 }.map { it.first }
                    predictionDiseaseName?.let { viewModel.addPredictionData(it, activeFeatures.joinToString(", "), false, "") }

                    isSaved = !isSaved
                }) {
                    Icon(
                        painter = painterResource(
                        id = if (isSaved) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24),
                        contentDescription = "saved",
                        tint = if (isSaved) blueColor5 else Color.Black
                    )
                }
            }
        }
    }
}

/*
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
}*/
