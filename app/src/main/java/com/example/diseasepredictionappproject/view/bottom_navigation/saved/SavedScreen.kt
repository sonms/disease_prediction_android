package com.example.diseasepredictionappproject.view.bottom_navigation.saved

import android.os.Build
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.R
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.ui.theme.blueColor1
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.result.MedicineInfoItem
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SavedScreen(
    navController: NavController,
    viewModel : PredictionViewModel = hiltViewModel(),
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)

    //보여질 데이터 관련
    val allPredictionData by viewModel.allPredictionsData.collectAsState(initial = emptyList())
    val allMedicineData by medicineViewModel.allMedicineData.collectAsState(initial = emptyList())

    var isShow by remember {
        mutableStateOf(false)
    }
    var deleteItem by remember {
        mutableStateOf<PredictionEntity?>(null)
    }
    var deleteMedicineItem by remember {
        mutableStateOf<MedicineEntity?>(null)
    }

    //tab 레이아웃 관련
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("질병", "약")
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size }
    )
    val tabIndex = pagerState.currentPage

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = tabIndex,

        ) {
            tabs.forEachIndexed { index, value ->
                Tab(
                    modifier = Modifier.weight(1f),
                    selected = tabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center // Text를 가운데 정렬
                    ) {
                        Text(
                            text = value,
                            modifier = Modifier.padding(16.dp),
                            style = FontUtils.getTextStyle(fontSize.size + 4f)
                        )
                    }
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
        ) {page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center // 페이저 내용을 가운데 정렬
            ) {
                val currentData = when (page) {
                    0 -> allPredictionData
                    1 -> allMedicineData
                    else -> emptyList()
                }

                if (currentData.isEmpty()) {
                    Text(
                        text = when (page) {
                            0 -> "저장한 질병 데이터가 존재하지 않습니다."
                            1 -> "저장한 약 데이터가 존재하지 않습니다."
                            else -> ""
                        },
                        textAlign = TextAlign.Center, // 텍스트 가운데 정렬
                        style = FontUtils.getTextStyle(fontSize.size + 4f),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.align(Alignment.TopCenter),
                        contentPadding = PaddingValues(vertical = 10.dp),
                    ) {
                        itemsIndexed(
                            items = currentData
                        ) { _, item ->
                            when (page) {
                                0 -> { // "질병" 탭에서의 아이템
                                    SavedItem(
                                        item as PredictionEntity,
                                        onClick = {
                                            navController.navigate("detail?id=${item.id}")
                                        },
                                        onLongClick = {
                                            deleteItem = it
                                            isShow = !isShow
                                        },
                                        onStarClick = {
                                            viewModel.updateOnlySpecificData(
                                                id = item.id,
                                                isBookMark = !(item.isBookMark ?: false)
                                            )
                                        },
                                        fontSize
                                    )
                                }
                                1 -> { // "약" 탭에서의 아이템
                                    var formatItem : Item?

                                    (item as MedicineEntity).let {
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
                                        data = item as MedicineEntity, // 타입 캐스팅 필요
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
                                            isShow = !isShow
                                        },
                                        onCheckClick = {
                                            medicineViewModel.updateOnlySpecificMedicineData(
                                                id = item.id,
                                                isBookMark = !(item.isBookMark ?: false)
                                            )
                                        },
                                        fontSize
                                    )
                                }
                            }
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
                        viewModel.deletePredictionData(it)
                    }
                    deleteMedicineItem = null
                },
                onCancelClick = {
                    isShow = !isShow
                    deleteMedicineItem = null
                },
                fontSize
            )
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
                },
                fontSize
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedItem(
    data : PredictionEntity,
    onClick : (PredictionEntity) -> Unit, // 정보확인용
    onLongClick : (PredictionEntity) -> Unit, //삭제용
    onStarClick : (Boolean) -> Unit,
    fontSize: FontSize
) {
    var isClickStar by remember {
        mutableStateOf(data.isBookMark)
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(blueColor4, RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {
                    onClick(data)
                },
                onLongClick = {
                    onLongClick(data)
                }
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
               Text(text = data.diseaseName.toString(), fontWeight = FontWeight.Bold, style = FontUtils.getTextStyle(fontSize.size + 4f))

               Spacer(modifier = Modifier.height(8.dp))

               Text(
                   text = "Date: ${data.createDate.split("T").first()}",
                   style = FontUtils.getTextStyle(fontSize.size - 2f),
                   //color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
               )
           }

           Spacer(modifier = Modifier.weight(1f))

           IconButton(
               onClick = {
                   isClickStar = !isClickStar!!

                   onStarClick(isClickStar!!)
               }
           ) {
               Icon(
                   painter = painterResource(
                   id = if (isClickStar!!) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24),
                   contentDescription = "bookmark",
               )
           }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MedicineItem(
    data : MedicineEntity,
    isChecked : Boolean,
    onClick : (MedicineEntity) -> Unit,
    onLongClick : (MedicineEntity) -> Unit,
    onCheckClick : (Boolean) -> Unit,
    fontSize: FontSize
) {
    var isClickStar by remember {
        mutableStateOf(data.isBookMark)
    }


    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(blueColor4, RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = {
                    onClick(data)
                },
                onLongClick = {
                    onLongClick(data)
                }
            )
            .padding(10.dp)
    ) {
        Row (
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column (
                modifier = Modifier.weight(1f)
            ) {
                //약 이름
                Text(text = data.itemName.toString(), fontWeight = FontWeight.Bold, style = FontUtils.getTextStyle(fontSize.size + 4f))

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = data.entpName.toString(), fontWeight = FontWeight.SemiBold, style = FontUtils.getTextStyle(fontSize.size - 2f))

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "품목허가일 : ${data.openDe}",
                    style = FontUtils.getTextStyle(fontSize.size - 2f),
                    //color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))

            IconButton(
                modifier = Modifier.weight(0.3f),
                onClick = {
                    isClickStar = !isClickStar!!

                    onCheckClick(isClickStar!!)
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isClickStar!!) R.drawable.baseline_star_24 else R.drawable.baseline_star_border_24),
                    contentDescription = "bookmark",
                )
            }
            /*Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckClick
            )*/
        }
    }
}

@Composable
fun DeleteItemDialog(
    item : PredictionEntity?,
    onConfirmClick : (PredictionEntity?) -> Unit,
    onCancelClick : () -> Unit,
    fontSize: FontSize
) {
    Dialog(
        onDismissRequest = { onCancelClick() }
    ) {
        Card (
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .padding(10.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 10.dp),
                text = "정말 삭제하시겠습니까?",
                fontWeight = FontWeight.SemiBold,
                style = FontUtils.getTextStyle(fontSize.size + 4f)
            )

            Row (
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(20.dp)
            ) {
                Button(
                    modifier = Modifier.padding(end = 5.dp),
                    onClick = { onCancelClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueColor4, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(text = "취소", style = FontUtils.getTextStyle(fontSize.size - 2f))
                }

                Button(
                    onClick = { onConfirmClick(item) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueColor4, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(text = "확인", style = FontUtils.getTextStyle(fontSize.size - 2f))
                }
            }
        }
    }
}

@Composable
fun DeleteMedicineItemDialog(
    item : MedicineEntity?,
    onConfirmClick : (MedicineEntity?) -> Unit,
    onCancelClick : () -> Unit,
    fontSize: FontSize
) {
    Dialog(
        onDismissRequest = { onCancelClick() }
    ) {
        Card (
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .padding(10.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 10.dp),
                text = "정말 삭제하시겠습니까?",
                style = FontUtils.getTextStyle(fontSize.size + 4f),
                fontWeight = FontWeight.SemiBold
            )

            Row (
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(20.dp)
            ) {
                Button(
                    modifier = Modifier.padding(end = 5.dp),
                    onClick = { onCancelClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueColor4, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(text = "취소", style = FontUtils.getTextStyle(fontSize.size - 2f))
                }

                Button(
                    onClick = { onConfirmClick(item) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueColor4, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(text = "확인", style = FontUtils.getTextStyle(fontSize.size - 2f))
                }
            }
        }
    }
}