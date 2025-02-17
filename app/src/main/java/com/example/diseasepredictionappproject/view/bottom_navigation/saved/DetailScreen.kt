package com.example.diseasepredictionappproject.view.bottom_navigation.saved

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.BuildConfig
import com.example.diseasepredictionappproject.data.DrugInfoRequest
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.loading.LoadingState
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.ui.theme.Pink100
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.OpenApiViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    id : Long?,
    type : String?,
    navController: NavController,
    viewModel: PredictionViewModel = hiltViewModel(),
    openApiViewModel: OpenApiViewModel = hiltViewModel(),
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)

    LaunchedEffect(id) {
        viewModel.setSelectedId(id)
    }

    val selectData by viewModel.selectedSavedItem.collectAsState()
    val openApiUiState by openApiViewModel.openApiUiState.collectAsState()
    val movedMedicineData by medicineViewModel.medicineMoveData.collectAsState()

    //약 추천받기
    var isStartRecommendMedicine by remember {
        mutableStateOf(false)
    }

    var clickData by remember {
        mutableStateOf<DrugInfoRequest?>(null)
    }



    LaunchedEffect(isStartRecommendMedicine) {
        clickData?.let {
            openApiViewModel.fetchDrugInfo(
                serviceKey = it.serviceKey,
                efcyQesitm = "발열",
                type = "json"
            )
        }
    }

    /*LaunchedEffect(movedMedicineData) {
        Log.e("movedMedicineData", movedMedicineData.toString())
    }*/

    when (openApiUiState) {
        is OpenApiViewModel.OpenApiUiState.Loading -> {
            CoroutineScope(Dispatchers.IO).launch {
                LoadingState.show()
                try {
                    // 비동기 작업 수행
                } finally {
                    LoadingState.hide()
                }
            }
        }
        is OpenApiViewModel.OpenApiUiState.Success -> {
            val drugInfo = (openApiUiState as OpenApiViewModel.OpenApiUiState.Success).data
            //Log.e("drugInfo", drugInfo.toString())
            medicineViewModel.updateMedicineResultData(drugInfo)

            openApiViewModel.fetchOpenApiUIState(OpenApiViewModel.OpenApiUiState.Wait)

            LoadingState.hide()
            navController.navigate("result")
        }
        is OpenApiViewModel.OpenApiUiState.Error -> {
            val error = (openApiUiState as OpenApiViewModel.OpenApiUiState.Error).message
            Log.e("drugInfoError", error.toString())
            Text(text = "에러 발생: $error")
        }
        is OpenApiViewModel.OpenApiUiState.Wait -> {
            //state 대기용
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .wrapContentSize()
                        .align(Alignment.Top),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "backScreen")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                if (type == "disease") {
                    clipboardManager.setText(AnnotatedString("질병 이름 : ${selectData?.diseaseName} / 선택 증상 : ${selectData?.diseaseContent}"))

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, clipboardManager.getText())
                        this.type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "공유하기")
                    context.startActivity(shareIntent)
                } else {
                    clipboardManager.setText(AnnotatedString("약 이름 : ${movedMedicineData?.itemName} / 효능 : ${movedMedicineData?.efcyQesitm}"))

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, clipboardManager.getText())
                        this.type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "공유하기")
                    context.startActivity(shareIntent)
                }
            }) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .wrapContentSize()
                        .align(Alignment.Top),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(Icons.Default.Share, contentDescription = "share content")
                }
            }
        }


        if (type == "disease") {
            DetailItem(
                item = selectData,
                fontSize = fontSize,
                onSearchMedicine = {
                        isStartRecommendMedicine = !isStartRecommendMedicine

                        val requestData = DrugInfoRequest (
                            /*@SerializedName("ServiceKey") val serviceKey: String, // 인증키
                @SerializedName("pageNo") val pageNo: Int? = null, // 페이지 번호 (옵션)
                @SerializedName("numOfRows") val numOfRows: Int? = null, // 한 페이지 결과 수 (옵션)
                @SerializedName("entpName") val entpName: String? = null, // 업체명 (옵션)
                @SerializedName("itemName") val itemName: String? = null, // 제품명 (옵션)
                @SerializedName("itemSeq") val itemSeq: String? = null, // 품목기준코드 (옵션)
                @SerializedName("efcyQesitm") val efcyQesitm: String? = null, // 문항1(효능) (옵션)
                @SerializedName("useMethodQesitm") val useMethodQesitm: String? = null, // 문항2(사용법) (옵션)
                @SerializedName("atpnWarnQesitm") val atpnWarnQesitm: String? = null, // 문항3(주의사항경고) (옵션)
                @SerializedName("atpnQesitm") val atpnQesitm: String? = null, // 문항4(주의사항) (옵션)
                @SerializedName("intrcQesitm") val intrcQesitm: String? = null, // 문항5(상호작용) (옵션)
                @SerializedName("seQesitm") val seQesitm: String? = null, // 문항6(부작용) (옵션)
                @SerializedName("depositMethodQesitm") val depositMethodQesitm: String? = null, // 문항7(보관법) (옵션)
                @SerializedName("openDe") val openDe: String? = null, // 공개일자 (옵션)
                @SerializedName("updateDe") val updateDe: String? = null, // 수정일자 (옵션)
                @SerializedName("type") val type: String? = "json" // 데이터 포맷 (옵션, 기본값: xml)*/
                            serviceKey = BuildConfig.MEDICINE_API_KEY_D,
                            efcyQesitm = it.diseaseContent?.split(",")?.get(0),
                            type = "json"
                        )

                        clickData = requestData
                },
                onEditData = {
                    navController.navigate("edit?type=edit&data=prediction&id=${it.id}")
                }
            )
        } else {
            DetailMedicineItem(
                item = movedMedicineData,
                fontSize = fontSize
            ) {
                navController.navigate("edit?type=edit&data=medicine&id=${it?.bizrno?.toLong()}")
            }
        }
    }
}

@Composable
fun DetailItem(
    item: PredictionEntity?,
    fontSize: FontSize,
    onSearchMedicine: (PredictionEntity) -> Unit,
    onEditData: (PredictionEntity) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // 상단 (제목 + 카테고리)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item?.diseaseName.orEmpty(),
                fontWeight = FontWeight.Bold,
                style = FontUtils.getTextStyle(fontSize.size + 2f)
            )

            Text(
                text = "카테고리: 질병 설명",
                fontWeight = FontWeight.Bold,
                style = FontUtils.getTextStyle(fontSize.size),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 중앙 (내용)
        Text(
            text = item?.diseaseContent.orEmpty(),
            style = FontUtils.getTextStyle(fontSize.size + 4f),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        )

        // 하단 (버튼들)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { item?.let { onSearchMedicine(it) } },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blueColor4)
            ) {
                Text(
                    text = "약 찾아보기",
                    color = Color.White,
                    style = FontUtils.getTextStyle(fontSize.size)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { item?.let { onEditData(it) } },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blueColor4)
            ) {
                Text(
                    text = "수정하기",
                    color = Color.White,
                    style = FontUtils.getTextStyle(fontSize.size)
                )
            }
        }
    }
}

@Composable
fun DetailMedicineItem(
    item: Item?,
    fontSize: FontSize,
    onEditData: (Item?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // 상단: 제목, 제약회사, 카테고리
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item?.itemName.orEmpty(),
                fontWeight = FontWeight.Bold,
                style = FontUtils.getTextStyle(fontSize.size + 2f)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = item?.entpName.orEmpty(),
                    fontWeight = FontWeight.SemiBold,
                    style = FontUtils.getTextStyle(fontSize.size - 4f),
                    color = Color.Black.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "카테고리: 약 설명",
                    fontWeight = FontWeight.Bold,
                    style = FontUtils.getTextStyle(fontSize.size - 4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 중앙: 스크롤 가능한 효능, 복용법, 주의사항
        if (item?.atpnWarnQesitm.isNullOrEmpty()) {
            Text(
                text = "효능: ${item?.efcyQesitm.orEmpty()}",
                style = FontUtils.getTextStyle(fontSize.size),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(1f)
            )
            Spacer(modifier = Modifier.height(10.dp))
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "효능: ${item?.efcyQesitm.orEmpty()}",
                        style = FontUtils.getTextStyle(fontSize.size),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    item?.useMethodQesitm?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = "복용법: $it",
                            style = FontUtils.getTextStyle(fontSize.size - 2f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                item {
                    item?.atpnWarnQesitm?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = "주의사항1: $it",
                            color = Pink100,
                            style = FontUtils.getTextStyle(fontSize.size - 2f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                item {
                    item?.intrcQesitm?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = "주의사항2: $it",
                            color = Pink100,
                            style = FontUtils.getTextStyle(fontSize.size - 2f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        // 하단: 수정하기 버튼
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            onClick = { item?.let { onEditData(it) } },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blueColor4)
        ) {
            Text(
                text = "수정하기",
                color = Color.White,
                style = FontUtils.getTextStyle(fontSize.size)
            )
        }
    }
}