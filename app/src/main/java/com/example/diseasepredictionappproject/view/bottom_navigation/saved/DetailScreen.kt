package com.example.diseasepredictionappproject.view.bottom_navigation.saved

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.BuildConfig
import com.example.diseasepredictionappproject.data.DrugInfoRequest
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.loading.LoadingState
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.ui.theme.Pink100
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.OpenApiViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "backScreen")
                }
            }
        }


        if (type == "disease") {
            DetailItem(selectData) {
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
            }
        } else {
            DetailMedicineItem(
                item = movedMedicineData
            )
        }
    }
}

@Composable
fun DetailItem(
    item : PredictionEntity?,
    onBtnClick : (PredictionEntity) -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(5.dp)
        ) {
            Text(
                text = item?.diseaseName.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp,
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "카테고리 : 질병 설명", fontWeight = FontWeight.Bold, fontSize = 18.sp,
            )
        }

        Text(text = item?.diseaseContent.toString())


        Button(
            modifier = Modifier
                .padding(top = 20.dp),
            onClick = {
                if (item != null) {
                    onBtnClick(item)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blueColor4)
        ) {
            Text(text = "약 찾아보기", color = Color.White)
        }
    }
}

@Composable
fun DetailMedicineItem(
    item : Item?
) {
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column (
                modifier = Modifier.padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item?.itemName.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp,
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row (
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Text(
                        text = item?.entpName.toString(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Black.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "카테고리 : 약 설명", fontWeight = FontWeight.Bold, fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Text(text = "효능 : ${item?.efcyQesitm.toString()}", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            if (item?.useMethodQesitm?.isNotEmpty() == true) {
                Text(text = "복용법 ${item.useMethodQesitm}")

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        item {
            if (item?.atpnWarnQesitm?.isNotEmpty() == true) {
                Text(text = "주의사항1 ${item.atpnWarnQesitm}", color = Pink100)

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        item {
            if (item?.intrcQesitm?.isNotEmpty() == true) {
                Text(text = "주의사항2 ${item.intrcQesitm}", color = Pink100)

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        /*Button(
            modifier = Modifier
                .padding(top = 20.dp),
            onClick = {
                if (item != null) {
                    onBtnClick(item)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blueColor4)
        ) {
            Text(text = "약 찾아보기", color = Color.White)
        }*/
    }
}