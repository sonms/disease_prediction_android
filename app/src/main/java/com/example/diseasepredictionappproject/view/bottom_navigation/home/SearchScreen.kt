package com.example.diseasepredictionappproject.view.bottom_navigation.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.data.Item
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.MedicineItem
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.SavedItem
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchScreen(
    navController: NavController,
    predictionViewModel: PredictionViewModel = hiltViewModel(),
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {

    val predictionSearchData by predictionViewModel.predictionSearchData.collectAsState()
    val medicineSearchData by medicineViewModel.medicineSearchData.collectAsState()

    val allSearchData by remember(predictionSearchData, medicineSearchData) {
        derivedStateOf {
            predictionSearchData + medicineSearchData
        }
    }

    var searchValue by remember {
        mutableStateOf("")
    }

    //false가 검색창으로 변경, true = 최근검색어창
    var isRecentQuery by remember {
        mutableStateOf(true)
    }

    val context = LocalContext.current
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)
    val savedQuery by PreferenceDataStore.getSearchQueries(context).collectAsState(initial = emptyList())
    val focusRequester = remember { FocusRequester() } // FocusRequester 생성

    // 화면이 표시될 때 포커스를 주고 키보드를 띄움
    LaunchedEffect(Unit) {
        focusRequester.requestFocus() // 포커스를 요청
    }
    /*
    * val interactionSource = remember { MutableInteractionSource() }
    var isFocused by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }

    // 감지된 이벤트를 관찰하기 위해 효과 추가
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> isFocused = true
                is FocusInteraction.Unfocus -> isFocused = false
                is PressInteraction.Press -> isClicked = true
                is PressInteraction.Release -> isClicked = false
            }
        }
    }*/

    LaunchedEffect(searchValue) {
        predictionViewModel.searchPredictionData(searchValue)
        medicineViewModel.searchMedicineData(searchValue)
        Log.e("searchValue", predictionSearchData.toString() + medicineSearchData.toString())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically, // 수직 정렬: 가운데 정렬
            horizontalArrangement = Arrangement.Start // 가로 방향 정렬: 시작 정렬
        ) {
            // 뒤로가기 버튼
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp) // 아이콘 크기 조정
                )
            }

            SearchEditText(
                searchValue = searchValue,
                focusRequester = focusRequester,
                onTextChange = {
                   searchValue = it
                },
                onDone = {
                    isRecentQuery = false
                    CoroutineScope(Dispatchers.IO).launch {
                        PreferenceDataStore.saveSearchQuery(context, searchValue)
                    }
                },
                onClear = {
                    isRecentQuery = true
                }
            )
        }



        if (isRecentQuery) {
            if (savedQuery.isNotEmpty()) {
                Column {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "최근 검색어",
                            modifier = Modifier
                                .wrapContentSize(),
                            style = FontUtils.getTextStyle(fontSize.size + 2f)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "전체 삭제",
                            modifier = Modifier
                                .wrapContentSize()
                                .clickable {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        PreferenceDataStore.deleteAllSearchQuery(context)
                                    }
                                }
                            ,
                            style = FontUtils.getTextStyle(fontSize.size + 2f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyRow (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        contentPadding = PaddingValues(5.dp)
                    ) {
                        itemsIndexed (
                            items = savedQuery,
                        ) { _, query ->
                            QueryItem(
                                query = query,
                                fontSize = fontSize,
                                onClickQuery = {
                                    isRecentQuery = false
                                    searchValue = it
                                },
                                onDeleteQuery = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        PreferenceDataStore.deleteSearchQuery(context, it)
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                Text(text = "최근 검색어가 없습니다.")
            }
        } else {
            if (allSearchData.isNotEmpty()) {
                LazyColumn (
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    itemsIndexed(
                        items = allSearchData
                    ) { _, item ->
                        when (item) {
                            is PredictionEntity -> {
                                // PredictionEntity 타입의 데이터 처리
                                SavedItem(
                                    data = item,
                                    onClick = {
                                        navController.navigate("detail?id=${item.id}")
                                    },
                                    onLongClick = {

                                    },
                                    onStarClick = {
                                        predictionViewModel.updateOnlySpecificData(id = item.id, isBookMark = !item.isBookMark!!)
                                    },
                                    fontSize
                                )
                            }
                            is MedicineEntity -> {
                                // MedicineItem 타입의 데이터 처리
                                var formatItem : Item?

                                (item).let {
                                    formatItem = Item (
                                        /*val entpName: String?,
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
                                    data = item,
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
                            else -> {
                                // 기타 데이터 타입 처리 (필요에 따라 추가)
                                Text("Unknown Item Type")
                            }
                        }
                    }
                }
            } else {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "검색된 데이터가 없습니다.",
                        fontWeight = FontWeight.Bold,
                        style = FontUtils.getTextStyle(fontSize.size + 2f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchEditText(
    searchValue: String,
    focusRequester: FocusRequester,
    onTextChange: (String) -> Unit,
    onDone : (Boolean) -> Unit,
    onClear : () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = searchValue,
        onValueChange = { onTextChange(it) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp) // 뒤로가기 버튼과의 간격 조정
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
            .focusRequester(focusRequester)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done // 완료 버튼 표시
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onDone(true)
            }
        ),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = searchValue,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                isError = false,
                label = null,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                placeholder = {
                    Text(
                        text = "검색어를 입력하세요",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = null,
                trailingIcon = {
                    if (searchValue.isNotEmpty()) {
                        IconButton(onClick = {
                            onTextChange("")
                            onClear()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp)
            )
        }
    )
}

@Composable
fun QueryItem(
    query: String,
    fontSize: FontSize,
    onClickQuery: (String) -> Unit,
    onDeleteQuery: (String) -> Unit
) {
    AssistChip(
        modifier = Modifier
            .wrapContentSize()
            .padding(5.dp),
        onClick = { onClickQuery(query) },
        label = {
            Text(
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                text = query,
                style = FontUtils.getTextStyle(fontSize.size)
            )
        },
        trailingIcon = {
            Icon(Icons.Default.Close, contentDescription = "delete", modifier = Modifier
                .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
                .clickable {
                    onDeleteQuery(query)
                }
            )
        }
    )
}
