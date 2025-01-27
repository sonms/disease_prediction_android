package com.example.diseasepredictionappproject.view.bottom_navigation.saved.result

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.MainActivity
import com.example.diseasepredictionappproject.R
import com.example.diseasepredictionappproject.data.DrugInfoResponse
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.loading.LoadingState
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResultScreen(
    navController: NavController,
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)

    val resultMedicineData by medicineViewModel.medicineResultData.collectAsState()
    //체그 데이터 관리용
    val checkedState = remember { mutableStateMapOf<String, Boolean>() }

    var isLoadingDelay by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        delay(1500) // 1.5초 동안 대기
        isLoadingDelay = false // 로딩 종료
    }
    /*LaunchedEffect(resultMedicineData) {
        Log.e("ResultScreen", "Updated resultMedicineData: $resultMedicineData")
        delay(1500) // 1.5초 동안 대기
        isLoadingDelay = false // 로딩 종료
    }*/

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
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "backScreen")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (checkedState.isNotEmpty()) {
                IconButton(onClick = {
                    checkedState.forEach { (key, _) ->
                        val prevItem = resultMedicineData.find { it.itemSeq == key }
                        var formatEntity : MedicineEntity? = null

                        prevItem?.let {
                            formatEntity = MedicineEntity (
                                /*
                                * val isBookMark: Boolean?,
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
                                isBookMark = false,
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
                        formatEntity?.let { medicineViewModel.addPredictionData(it) }

                        navController.navigate(MainActivity.BottomNavItem.Saved.screenRoute)
                    }
                }) {
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .wrapContentSize()
                            .align(Alignment.Top),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Icon(Icons.Default.Done, contentDescription = "sendCheckData")
                    }
                }
            }
        }

        if (isLoadingDelay) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            LazyColumn (
                contentPadding = PaddingValues(10.dp)
            ) {
                itemsIndexed(
                    items = resultMedicineData
                ) { _, medicine ->
                    MedicineInfoItem(
                        data = medicine,
                        isChecked = checkedState[medicine.itemSeq] ?: false, // 초기 값은 false
                        fontSize = fontSize,
                        onClick = {
                            medicineViewModel.updateMedicineMoveData(medicine)
                            navController.navigate("detail?type=medicine")
                        },
                        onCheckClick = { isChecked ->
                            // 상태 업데이트
                            checkedState[medicine.itemSeq ?: ""] = isChecked
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MedicineInfoItem(
    data : Item,
    isChecked : Boolean,
    fontSize: FontSize,
    onClick : (Item) -> Unit,
    //onLongClick : (Item) -> Unit,
    onCheckClick : (Boolean) -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(blueColor4, RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {
                    onClick(data)
                },
                /*onLongClick = {
                    onLongClick(data)
                }*/
            )
            .padding(10.dp)
    ) {
        Row (
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column {
                //약 이름
                Text(text = "${data.entpName.toString()} ${data.itemName.toString()}", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "updateDate: ${data.updateDe}",
                    style = FontUtils.getTextStyle(fontSize.size),
                    //color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            /*IconButton(
                onClick = {
                    isClickStar = !isClickStar

                    onStarClick(isClickStar)
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isClickStar) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24),
                    contentDescription = "bookmark",
                )
            }*/
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckClick
            )
        }
    }
}