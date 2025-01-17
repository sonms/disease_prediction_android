package com.example.diseasepredictionappproject.view.bottom_navigation.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.data.HealthTipData
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.ui.theme.blueColor6
import com.example.diseasepredictionappproject.ui.theme.blueColor7
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.DeleteItemDialog
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.DeleteMedicineItemDialog
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.MedicineItem
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.SavedItem
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel


fun getHealthTips(): List<HealthTipData> {
    return listOf(
        HealthTipData(
            "15분간 짧은 운동하기",
            "이 짧은 운동으로도 만성질환의 위험을 높이는 콜레스테롤과 혈당 등의 수치를 조절하는 효과가 나타납니다."
        ),
        HealthTipData(
            "바깥 벤치에 잠깐 앉아있기",
            "불안감을 감소시키고, 뇌 건강을 촉진하는데도 도움이 됩니다."
        ),
        HealthTipData(
            "식사시간 5분 더 늘리기",
            "식사 속도를 늦춰 섭취량을 조절하면 복부팽만감이 나타나는 빈도도 줄일 수 있습니다."
        ),
        HealthTipData(
            "맞춤형 간식 챙기기",
            "카페인을 꾸준히 소비하는 사람들은 그렇지 않은 사람들보다 알츠하이머 위험이 현저히 낮았습니다."
        ),
        HealthTipData(
            "견과류 먹기",
            "콜레스테롤 수치를 조절해야 하는 사람이라면 나쁜 콜레스테롤인 LDL 수치는 낮추고, 좋은 콜레스테롤인 HDL 수치는 높이는 견과류를 먹는 것이 도움이 됩니다."
        ),
        HealthTipData(
            "의자에서 일어나서 스트레칭",
            "새로 발생하는 암 환자 중에 9만 건 이상이 움직이지 않고 오래 앉아있는 것이 주된 원인입니다."
        ),
        HealthTipData(
            "의식적으로 물 마시기",
            "충분한 수분을 섭취하면 체내 독소를 배출하고, 피부 건강을 유지하며, 에너지를 향상시킬 수 있습니다. 하루에 8잔 이상의 물을 마시는 것을 목표로 합시다."
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen( //건강관련팁, 질병정보검색, 최근예측결과요약
    navController : NavController,
    predictionViewModel: PredictionViewModel = hiltViewModel(),
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    var isSearchOpen by remember {
        mutableStateOf(false)
    }

    var healthTipData by remember {
        mutableStateOf<HealthTipData?>(null)
    }

    val latestPredictionData by predictionViewModel.latestPredictionData.collectAsState(initial = null)
    val bookMarkedData by predictionViewModel.bookMarkedPredictionData.collectAsState(initial = emptyList())
    val bookMarkedMedicineData by medicineViewModel.bookMarkedMedicineData.collectAsState(initial = emptyList())

    val allBookMarkedData by remember (bookMarkedData, bookMarkedMedicineData) {
        derivedStateOf {
            bookMarkedData + bookMarkedMedicineData
        }
    }

    //검색창으로 이동
    /*LaunchedEffect(isSearchOpen) {
        navController
    }*/

    var isShow by remember {
        mutableStateOf(false)
    }
    var deleteItem by remember {
        mutableStateOf<PredictionEntity?>(null)
    }
    var deleteMedicineItem by remember {
        mutableStateOf<MedicineEntity?>(null)
    }

    LaunchedEffect(Unit) {
        healthTipData= getHealthTips().shuffled()[0]
    }

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "질병 예측", modifier = Modifier.padding(start = 10.dp))
            
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                isSearchOpen = !isSearchOpen
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
        
        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            //최근 예측결과 요약
            item {
                RecentPredictionResult(latestPredictionData)
            }
            //건강관련팁
            item {
                HealthTip(healthTipData)
            }

            //북마크 데이터 보여주기
            if (allBookMarkedData.isEmpty()) {
                item {
                    Text(text = "북마크한 데이터가 없습니다.")
                }
            } else {
                itemsIndexed (
                    items = allBookMarkedData
                ) {_, bookMarkItem ->
                    when (bookMarkItem) {
                        is PredictionEntity -> {
                            // PredictionEntity 타입의 데이터 처리
                            SavedItem(
                                data = bookMarkItem as PredictionEntity,
                                onClick = {
                                    navController.navigate("detail?id=${bookMarkItem.id}")
                                },
                                onLongClick = {
                                    deleteItem = it
                                    isShow = !isShow
                                },
                                onStarClick = {
                                    predictionViewModel.updateOnlySpecificData(id = bookMarkItem.id, isBookMark = !bookMarkItem.isBookMark!!)
                                },
                            )
                        }
                        is MedicineEntity -> {
                            // MedicineItem 타입의 데이터 처리
                            var formatItem : Item?

                            (bookMarkItem).let {
                                formatItem = Item (
                                    /*
                                    val entpName: String?,
                                    val itemName: String?,
                                    val itemSeq: String?,
                                    val efcyQesitm: String?,
                                    val useMethodQesitm: String?,
                                    val atpnWarnQesitm: String?,
                                    val atpnQesitm: String?,
                                    val intrcQesitm: String?,
                                    val seQesitm: String?,
                                    val depositMethodQesitm: String?,
                                    val openDe: String?,
                                    val updateDe: String?,
                                    val itemImage: String?,
                                    val bizrno: String?*/
                                    entpName = it.entpName,
                                    itemName = it.itemName,
                                    itemSeq = it.itemSeq,
                                    efcyQesitm = it.efcyQesitm,
                                    useMethodQesitm = it.useMethodQesitm,
                                    atpnWarnQesitm = it.atpnWarnQesitm,
                                    atpnQesitm = it.atpnQesitm,
                                    intrcQesitm = it.intrcQesitm,
                                    seQesitm = it.seQesitm,
                                    depositMethodQesitm = it.depositMethodQesitm,
                                    openDe = it.openDe,
                                    updateDe = it.updateDe,
                                    itemImage = it.itemImage,
                                    bizrno = it.bizrno
                                )
                            }

                            MedicineItem(
                                data = bookMarkItem as MedicineEntity, // 타입 캐스팅 필요
                                isChecked = false,
                                onClick = {
                                    navController.navigate("detail?type=medicine")
                                    formatItem?.let { it1 ->
                                        medicineViewModel.updateMedicineMoveData(
                                            it1
                                        )
                                    }
                                },
                                onLongClick = { delete ->
                                    // 체크 로직 추가 가능
                                    deleteMedicineItem = delete
                                },
                                onCheckClick = {
                                    medicineViewModel.updateOnlySpecificMedicineData(
                                        id = bookMarkItem.id,
                                        isBookMark = !(bookMarkItem.isBookMark ?: false)
                                    )
                                }
                            )
                        }
                        else -> {
                            // 기타 데이터 타입 처리 (필요에 따라 추가)
                            Text("Unknown Item Type")
                        }
                    }
                }
            }
        }
    }

    if (isShow) {
        if (deleteItem != null) {
            DeleteItemDialog(deleteItem,
                onConfirmClick = {
                    isShow = !isShow
                    it?.let {
                        predictionViewModel.deletePredictionData(it)
                    }
                    deleteMedicineItem = null
                },
                onCancelClick = {
                    isShow = !isShow
                    deleteMedicineItem = null
                })
        }

        if (deleteMedicineItem != null) {
            DeleteMedicineItemDialog(deleteMedicineItem,
                onConfirmClick = {
                    isShow = !isShow
                    it?.let {
                        medicineViewModel.deleteMedicineData(it)
                    }
                    deleteMedicineItem = null
                },
                onCancelClick = {
                    isShow = !isShow
                    deleteMedicineItem = null
                })
        }
    }
}

@Composable
fun RecentPredictionResult(latestPrediction : PredictionEntity?) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "최근 예측 결과",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Column (
            modifier = Modifier.wrapContentSize(),
        ) {
            if (latestPrediction != null) {
                Text(text = "최근 예측 질병 : ${latestPrediction.diseaseName}", modifier = Modifier.padding(10.dp), color = blueColor7)

                Text(text = "${latestPrediction.diseaseContent}", modifier = Modifier.padding(10.dp), color = blueColor7)

                Text(text = "시행 날짜 : ${latestPrediction.createDate}", modifier = Modifier.padding(10.dp), color = blueColor7)
            } else {
                Text(text = "최근 예측 데이터가 없습니다.", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun HealthTip(healthTipData: HealthTipData?) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
    ) {
        Icon(
            painter = painterResource(id = com.example.diseasepredictionappproject.R.drawable.shiny_light_bulb_icon),
            contentDescription = "bulb"
        )

        Column (
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp)
        ) {
            Text(
                text = healthTipData?.tipTitle.toString(),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = healthTipData?.tipDescription.toString(),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

