package com.example.diseasepredictionappproject.view.bottom_navigation.edit

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.MainActivity
import com.example.diseasepredictionappproject.R
import com.example.diseasepredictionappproject.loading.LoadingState
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.ui.theme.blueColor2
import com.example.diseasepredictionappproject.ui.theme.blueColor3
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.ui.theme.blueColor6
import com.example.diseasepredictionappproject.utils.AlarmHelper
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

// 초기화 함수
/*fun initializeData(
    type: String,
    data: String,
    id: String,
    predictionViewModel: PredictionViewModel,
    medicineViewModel: MedicineViewModel,
    onInitialized: (String, String, String) -> Unit
) {
    LoadingState.show()

    when (type) {
        "default", "add" -> onInitialized("", "", "")
        else -> {
            when (data) {
                "prediction" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        predictionViewModel.setSelectedId(id.toLongOrNull())
                        val prediction = predictionViewModel.selectedSavedItem.value
                        onInitialized(
                            prediction?.diseaseName.orEmpty(),
                            prediction?.diseaseContent.orEmpty(),
                            "Prediction"
                        )
                    }
                }
                "medicine" -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        medicineViewModel.setSelectedId(id.toLongOrNull())
                        val medicine = medicineViewModel.selectedSavedItem.value
                        onInitialized(
                            medicine?.itemName.orEmpty(),
                            medicine?.efcyQesitm.orEmpty(),
                            "Medicine"
                        )
                    }
                }
            }
        }
    }
}*/

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditScreen(
    navController: NavController?,
    type : String, //edit, add
    data : String, //medicine, prediction
    id : String,
    predictionViewModel: PredictionViewModel = hiltViewModel(),
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val editPredictionData by predictionViewModel.selectedSavedItem.collectAsState()
    val editMedicineData by medicineViewModel.selectedSavedItem.collectAsState()

    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)

    //알람
    val alarmHelper = remember { AlarmHelper(context) }

    val calendar = Calendar.getInstance()
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_WEEK)) }
    val requestCode = 1001

    var isOpenCalendar by remember {
        mutableStateOf(false)
    }

    var isOpenTime by remember {
        mutableStateOf(false)
    }

    var isError by remember {
        mutableStateOf(false)
    }

    var isTypeError by remember {
        mutableStateOf(false)
    }

    var editTitle by remember {
        mutableStateOf("")
    }

    var editType by remember {
        mutableStateOf("")
    }

    var editContent by remember {
        mutableStateOf("")
    }

    var editSelectedDate by remember {
        mutableStateOf("")
    }

    var editSelectedTime by remember {
        mutableStateOf("")
    }

    var alarmContent by remember {
        mutableStateOf("")
    }


    /*if (type == "default" || type == "add") {
        LaunchedEffect(Unit) {
            editTitle = ""
            editContent = ""
            editType = ""
        }
    } else {
        if (data == "prediction") {
            LaunchedEffect(Unit) {
                predictionViewModel.setSelectedId(id = id.toLongOrNull())
                editType = "Prediction"
            }
        } else {
            LaunchedEffect(Unit) {
                medicineViewModel.setSelectedId(id = id.toLongOrNull())
                editType = "Medicine"
            }
        }
    }


    if (data == "prediction") {
        LaunchedEffect(editPredictionData) {
            editTitle = editPredictionData?.diseaseName ?: ""
            editContent = editPredictionData?.diseaseContent ?: ""

            Log.e("editTest1", editTitle.toString())
            Log.e("editTest1", editContent.toString())
        }
    } else {
        LaunchedEffect(editMedicineData) {
            editTitle = editMedicineData?.itemName ?: ""
            editContent = editMedicineData?.efcyQesitm ?: ""

            Log.e("editTest2", editTitle.toString())
            Log.e("editTest2", editContent.toString())
        }
    }*/
    LaunchedEffect(Unit) {
        LoadingState.show()
    }

    when (type) {
        "default", "add" -> {
            LaunchedEffect(Unit) {
                editTitle = ""
                editContent = ""
                editType = ""

                LoadingState.hide()
            }
        }
        else -> {
            when (data) {
                "prediction" -> {
                    LaunchedEffect(editPredictionData) {
                        predictionViewModel.setSelectedId(id = id.toLongOrNull())
                        editType = "Prediction"

                        editTitle = editPredictionData?.diseaseName ?: ""
                        editContent = editPredictionData?.diseaseContent ?: ""
                        Log.e("editTest1", editTitle.toString())
                        Log.e("editTest1", editContent.toString())

                        LoadingState.hide()
                    }
                }
                "medicine" -> {
                    LaunchedEffect(editMedicineData) {
                        medicineViewModel.setSelectedId(id = id.toLongOrNull())
                        editType = "Medicine"

                        editTitle = editMedicineData?.itemName ?: ""
                        editContent = editMedicineData?.efcyQesitm ?: ""

                        Log.e("editTest2", editTitle.toString())
                        Log.e("editTest2", editContent.toString())

                        LoadingState.hide()
                    }
                }
            }
        }
    }

    /*LaunchedEffect(type, data, id) {
        initializeData(type, data, id, predictionViewModel, medicineViewModel) { title, content, itemType ->
            Log.e("EditScreenData", "title: $title, content: $content")
            editTitle = title
            editContent = content
            editType = itemType
        }
        LoadingState.hide()
    }

    // 디버깅 로그 (필요 시 유지)
    Log.e("EditScreenData", "Prediction: $editTitle, Medicine: $editContent")*/





    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController?.popBackStack() }) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .wrapContentSize()
                        .align(Alignment.Top),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = "backScreen")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                if (editType.isNotEmpty() && editTitle.isNotEmpty()) {
                    if (editType == "Prediction") {
                        when (type) {
                            "add" -> {
                                predictionViewModel.addPredictionData(
                                    editTitle,
                                    editContent,
                                    false,
                                    ""
                                )
                            }

                            "edit" -> {
                                predictionViewModel.updatePredictionData(
                                    id = editPredictionData?.id!!,
                                    diseaseName = editTitle,
                                    diseaseContent = editContent,
                                    createDate = editPredictionData?.createDate,
                                    isBookMark = editPredictionData?.isBookMark,
                                    recommendMedication = editPredictionData?.recommendMedication
                                )
                            }
                        }
                    } else if (editType == "Medicine") {
                        when (type) {
                            "add" -> {
                                medicineViewModel.addPredictionData(
                                    MedicineEntity(
                                        entpName = "",
                                        itemName = editTitle,
                                        itemSeq = "",
                                        efcyQesitm = editContent,
                                        useMethodQesitm = "",
                                        atpnWarnQesitm = "",
                                        atpnQesitm = "",
                                        intrcQesitm = "",
                                        seQesitm = "",
                                        depositMethodQesitm = "",
                                        openDe = LocalDate.now().toString(),
                                        updateDe = LocalDate.now().toString(),
                                        itemImage = "",
                                        bizrno = "",
                                        isBookMark = false
                                    )
                                )
                            }

                            "edit" -> {
                                medicineViewModel.updateMedicineData(
                                    MedicineEntity(
                                        entpName = editMedicineData?.entpName,
                                        itemName = editTitle,
                                        itemSeq = editMedicineData?.itemSeq,
                                        efcyQesitm = editContent,
                                        useMethodQesitm = editMedicineData?.useMethodQesitm,
                                        atpnWarnQesitm = editMedicineData?.atpnWarnQesitm,
                                        atpnQesitm = editMedicineData?.atpnQesitm,
                                        intrcQesitm = editMedicineData?.intrcQesitm,
                                        seQesitm = editMedicineData?.seQesitm,
                                        depositMethodQesitm = editMedicineData?.depositMethodQesitm,
                                        openDe = editMedicineData?.openDe,
                                        updateDe = editMedicineData?.updateDe,
                                        itemImage = editMedicineData?.itemImage,
                                        bizrno = editMedicineData?.bizrno,
                                        isBookMark = editMedicineData?.isBookMark
                                    )
                                )
                            }
                        }
                    }

                    val message = alarmContent.ifEmpty {
                        editTitle
                    }

                    Log.e("editSelectedTime", editSelectedTime)

                    alarmHelper.scheduleWeeklyAlarm(selectedDay, editSelectedTime.split(":").first().trim().toInt(), editSelectedTime.split(":").last().trim().toInt(), requestCode, "알람", message)

                    val targetRoute = when (editType) {
                        "Home" -> MainActivity.BottomNavItem.Home.screenRoute
                        "Saved" -> MainActivity.BottomNavItem.Saved.screenRoute
                        else -> MainActivity.BottomNavItem.Home.screenRoute
                    }

                    navController?.navigate(targetRoute) {
                        popUpTo(targetRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                } else {
                    if (editTitle.isEmpty()) {
                        isError = true
                    }

                    if (editType.isEmpty()) {
                        isTypeError = true
                    }
                    /*when {
                        editTitle.isEmpty() -> {
                            isError = true
                        }

                        editType.isEmpty() -> {
                            isTypeError = true
                        }
                    }*/
                }
            }) {
                Icon(Icons.Default.Check, contentDescription = "edit complete")
            }
        }


        LazyColumn {
            item {
                //제목
                EditTitle(
                    textTitle = editTitle,
                    onTextChange = {
                        editTitle = it
                    },
                    isError = isError,
                    fontSize
                )
            }

            item {
                EditType(
                    fontSize,
                    isTypeError,
                    onClickType = {
                        editType = it
                    }
                )
            }

            item {
                EditContent(
                    type = "editContent",
                    editContent,
                    onTextChange = {
                        editContent = it
                    },
                    fontSize
                )
            }

            item {
                EditSchedule(
                    fontSize,
                    editSelectedDate,
                    editSelectedTime,
                    alarmContent,
                    onCalendarClick = {
                        isOpenCalendar = it
                    },
                    onClickDay = {
                        if (it != "") {
                            if (editSelectedDate != "") {
                                editSelectedDate = ""
                                editSelectedDate += "매주 ${it.split(" ").first()}요일"
                                selectedDay = it.split(" ").last().toInt()
                            } else {
                                editSelectedDate += "매주 ${it.split(" ").first()}요일"
                                selectedDay = it.split(" ").last().toInt()
                            }
                        } else {
                            editSelectedDate = ""
                        }
                    },
                    onClickTime = {
                        isOpenTime = it
                    },
                    onTextChange = {
                        alarmContent = it
                    }
                )
            }
        }
    }

    if (isOpenCalendar) {
        EditCalendar(
            selectDate = editSelectedDate,
            isOpenCalendar = isOpenCalendar,
            fontSize = fontSize,
            onClickCancel = {
                isOpenCalendar = false
            },
            onClickConfirm = {
                editSelectedDate = it
                isOpenCalendar = false
            }
        )
    }

    if (isOpenTime) {
        EditTime (
            showTimePicker = isOpenTime,
            fontSize = fontSize,
            onClickConfirm = {
                isOpenTime = false
                editSelectedTime = it
            },
            onClickCancel = {
                isOpenTime = false
            }
        )
    }
}

@Composable
fun EditTitle(
    textTitle: String,
    onTextChange: (String) -> Unit,
    isError : Boolean,
    fontSize: FontSize
) {
    val isErrorCheck by rememberSaveable { mutableStateOf(isError) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = textTitle,
        onValueChange = {
            onTextChange(it)
            //validate(it)
        },
        trailingIcon = {
            if (isError)
                Icon(Icons.Default.Info,"error", tint = MaterialTheme.colorScheme.error)
        },
        singleLine = false,
        textStyle = FontUtils.getTextStyle(fontSize.size),
        isError = isErrorCheck,
        /*trailingIcon = {
            if (isError)
                Icon(Icons.Filled.Info, "빈 칸을 채워주세요!", tint = MaterialTheme.colorScheme.error)
        },*/
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = blueColor4
        ),
        label = {
            Text (
                text = "질병 혹은 약 이름을 적어주세요",
                fontWeight = FontWeight.Bold,
                style = FontUtils.getTextStyle(fontSize.size -2f)
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done // 완료 버튼 표시
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        supportingText = {
            if (isError) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "질병 혹은 약 이름을 입력하세요.",
                    color = MaterialTheme.colorScheme.error,
                    style = FontUtils.getTextStyle(fontSize.size)
                )
            }
        },
    )
}

@Composable
fun EditType (
    fontSize: FontSize,
    isError: Boolean,
    onClickType : (String) -> Unit
) {
    var selectedType by remember { mutableStateOf<String?>(null) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // 질병 버튼
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    selectedType = "Prediction" // 선택된 타입을 업데이트
                    onClickType("Prediction")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == "Prediction") blueColor4 else Color.Gray
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "질병", style = FontUtils.getTextStyle(fontSize.size))
                    if (selectedType == "Prediction") {
                        Spacer(modifier = Modifier.width(4.dp)) // 텍스트와 체크 아이콘 간격
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = blueColor6
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 약 버튼
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    selectedType = "Medicine" // 선택된 타입을 업데이트
                    onClickType("Medicine")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == "Medicine") blueColor4 else Color.Gray
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "약", style = FontUtils.getTextStyle(fontSize.size))
                    if (selectedType == "Medicine") {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = blueColor6
                        )
                    }
                }
            }
        }

        if (isError) {
            Text(
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
                text = "유형을 선택해주세요.",
                style = FontUtils.getTextStyle(fontSize.size),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun EditContent(
    type : String,
    textContent: String,
    onTextChange: (String) -> Unit,
    fontSize : FontSize
) {
    val focusManager = LocalFocusManager.current
    val typeContentText by remember {
        mutableStateOf( if (type == "editContent") {
            "필요한 내용을 적어주세요 (선택사항)"
        } else {
            "알람에 필요한 내용을 적어주세요 (선택사항)"
        })
    }


    OutlinedTextField(
        value = textContent,
        onValueChange = onTextChange,
        singleLine = false,
        textStyle = FontUtils.getTextStyle(fontSize.size),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp, max = 200.dp) // 최대 높이 제한
            .verticalScroll(rememberScrollState()) // 내용이 넘칠 때 스크롤 가능
            .padding(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = blueColor6
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done // 완료 버튼 표시
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        label = {
            Text(
                text = typeContentText,
                fontWeight = FontWeight.Bold,
                style = FontUtils.getTextStyle(fontSize.size),
                color = Color.Black.copy(alpha = 0.5f)
            )
        }
    )
}

@Composable
fun EditSchedule(
    fontSize: FontSize,
    selectedDate : String,
    selectedTime : String,
    alarmContent : String,
    onCalendarClick: (Boolean) -> Unit,
    onClickDay : (String) -> Unit,
    onClickTime : (Boolean) -> Unit,
    onTextChange: (String) -> Unit
) {
    var isOpenAlarm by remember {
        mutableStateOf(false)
    }

    var alarmMessage by remember {
        mutableStateOf(alarmContent)
    }

    val dayItem = listOf("일","월","화","수","목","금","토")
    var selectDay by remember {
        mutableStateOf("")
    }
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //알림설정
        Text(text = "알림 설정", style = FontUtils.getTextStyle(fontSize.size), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isOpenAlarm,
            onCheckedChange = {
                isOpenAlarm = it
            },
            colors = SwitchDefaults.colors (
                checkedTrackColor = blueColor4,
                uncheckedTrackColor = Color.LightGray,
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White,
                uncheckedBorderColor = Color.LightGray
            )
        )
    }

    if (isOpenAlarm) {
        //날짜 설정
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedDate != "") {
                    Text(text = "선택 날짜 : $selectedDate", style = FontUtils.getTextStyle(fontSize.size), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                } else {
                    Text(text = "날짜 설정", style = FontUtils.getTextStyle(fontSize.size), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { onCalendarClick(true) }) {
                    Icon(Icons.Default.DateRange, contentDescription = "date select")
                }
            }



            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center // Row 내부 요소들도 중앙 정렬
            ) {
                for (i in dayItem) {
                    val textColor = when (i) {
                        "일" -> Color.Red
                        "토" -> Color.Blue
                        else -> Color.Black
                    }

                    Text(
                        text = i,
                        style = FontUtils.getTextStyle(fontSize.size),
                        color = textColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable(
                                //기본 파장 효과제거
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                selectDay = if (selectDay == i) {
                                    onClickDay("")
                                    ""
                                } else {
                                    onClickDay("$i ${dayItem.indexOf(i)}")
                                    i
                                }
                            }
                            .background(
                                if (selectDay == i) {
                                    blueColor4
                                } else {
                                    Color.Transparent
                                }, RoundedCornerShape(8.dp)
                            ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedTime != "") {
                    Text(text = "선택 시간 : $selectedTime", style = FontUtils.getTextStyle(fontSize.size) , fontWeight = FontWeight.Bold)
                } else {
                    Text(text = "시간 설정", style = FontUtils.getTextStyle(fontSize.size) , fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { onClickTime(true) }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_access_time_24), contentDescription = "time select")
                }
            }
        }


        EditContent (
            type = "alarmContent",
            alarmContent,
            onTextChange = {
                alarmMessage = it
                onTextChange(it)
            },
            fontSize
        )
    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
fun EditCalendar(
    selectDate: String?,
    isOpenCalendar: Boolean,
    fontSize: FontSize,
    onClickCancel: () -> Unit,
    onClickConfirm: (yyyyMMddHHmm: String) -> Unit
) {
    var isOpen by remember {
        mutableStateOf(isOpenCalendar)
    }

    if (isOpen) {
        var selectedDate by remember { mutableStateOf<String?>(LocalDate.now().toString()) }
        // DatePickerDialog (날짜 선택)
        DatePickerDialog(
            modifier = Modifier.wrapContentSize(),
            onDismissRequest = {
                onClickCancel()
                isOpen = false
            },
            confirmButton = {

            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                weekdayContentColor = Color.Black,
                titleContentColor = Color.Black,
                disabledDayContentColor = blueColor2,
                dayContentColor = Color.Black,
                todayDateBorderColor = blueColor4,
                selectedDayContainerColor = blueColor3
            ),
            shape = RoundedCornerShape(6.dp)
        ) {
            val currentDate = LocalDate.now()
            val datePickerState = rememberDatePickerState(
                yearRange = currentDate.year..currentDate.year + 1,
                initialDisplayMode = DisplayMode.Picker,
                initialSelectedDateMillis = System.currentTimeMillis()
            )

            DatePicker(state = datePickerState)

            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            onClickCancel()
                            isOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = blueColor4, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "취소", style = FontUtils.getTextStyle(fontSize.size))
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(Date(selectedDateMillis))
                            }
                            isOpen = false
                            onClickConfirm(selectedDate.toString())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = blueColor4, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "확인",style = FontUtils.getTextStyle(fontSize.size))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTime(
    showTimePicker: Boolean,
    fontSize : FontSize,
    onClickConfirm: (String) -> Unit,
    onClickCancel: () -> Unit
) {
    var isShowTimePicker by remember { mutableStateOf(showTimePicker) }

    val calendar = Calendar.getInstance()
    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }
    val timeState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute
    )

    if (isShowTimePicker) {
        Dialog(
            onDismissRequest = {
                onClickCancel()
                isShowTimePicker = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false // 플랫폼 기본 너비를 사용하지 않음
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onClickCancel()
                        isShowTimePicker = false
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timeState)

                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            onClickCancel()
                            isShowTimePicker = false
                        }) {
                            Text(
                                text = "취소",
                                style = FontUtils.getTextStyle(fontSize.size),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(onClick = {
                            selectedHour = timeState.hour
                            selectedMinute = timeState.minute
                            val formatTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                            onClickConfirm(formatTime)
                            isShowTimePicker = false
                        }) {
                            Text(
                                text = "확인",
                                style = FontUtils.getTextStyle(fontSize.size),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getDayString(day: Int): String {
    return when (day) {
        Calendar.SUNDAY -> "일요일"
        Calendar.MONDAY -> "월요일"
        Calendar.TUESDAY -> "화요일"
        Calendar.WEDNESDAY -> "수요일"
        Calendar.THURSDAY -> "목요일"
        Calendar.FRIDAY -> "금요일"
        Calendar.SATURDAY -> "토요일"
        else -> "알 수 없음"
    }
}

/*
@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
fun EditPreview() {
    EditScreen(navController = null, type = "add", data = "prediction", id = "-1")
}*/
