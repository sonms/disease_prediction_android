package com.example.diseasepredictionappproject.view.bottom_navigation.edit

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
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

                    alarmHelper.scheduleWeeklyAlarm(selectedDay, selectedHour, selectedMinute, requestCode, "반복 알람", "매주 ${getDayString(selectedDay)} ${selectedHour}:${selectedMinute} 알람")

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
                    when {
                        editTitle.isEmpty() -> {
                            isError = true
                        }

                        editType.isEmpty() -> {
                            isTypeError = true
                        }
                    }
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
            context = context,
            onClickConfirm = {
                isOpenTime = false
                editSelectedDate += " / $it"
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
    textContent: String,
    onTextChange: (String) -> Unit,
    fontSize : FontSize
) {
    val focusManager = LocalFocusManager.current

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
                text = "필요한 내용을 적어주세요 (선택사항)",
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
    onCalendarClick: (Boolean) -> Unit,
    onClickDay : (String) -> Unit,
    onClickTime : (Boolean) -> Unit
) {
    var isOpenAlarm by remember {
        mutableStateOf(false)
    }

    val dayItem = listOf("일","월","화","수","목","금","토")
    var isSelectDay by remember {
        mutableStateOf("")
    }
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "알림 설정", style = FontUtils.getTextStyle(fontSize.size))

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
                uncheckedThumbColor = Color.White
            )
        )
    }

    if (isOpenAlarm) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
        ) {
            Row {
                if (selectedDate != "") {
                    Text(text = "선택 날짜 : $selectedDate", style = FontUtils.getTextStyle(fontSize.size))
                } else {
                    Text(text = "날짜 설정", style = FontUtils.getTextStyle(fontSize.size))
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
                            .clickable {
                                isSelectDay = if (isSelectDay == i) {
                                    onClickDay("")
                                    ""
                                } else {
                                    onClickDay("$i ${dayItem.indexOf(i)}")
                                    i
                                }
                            }
                    )
                }
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (selectedDate != "") {
                    Text(text = "선택 시간 : $selectedDate", style = FontUtils.getTextStyle(fontSize.size))
                } else {
                    Text(text = "시간 설정", style = FontUtils.getTextStyle(fontSize.size))
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { onClickTime(true) }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_access_time_24), contentDescription = "time select")
                }
            }
        }
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
    if (isOpenCalendar) {
        var selectedDate by remember { mutableStateOf<String?>(LocalDate.now().toString()) }
        // DatePickerDialog (날짜 선택)
        DatePickerDialog(
            modifier = Modifier.wrapContentSize(),
            onDismissRequest = { onClickCancel() },
            confirmButton = {},
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
                        onClick = { onClickCancel() },
                        colors = ButtonDefaults.buttonColors(containerColor = blueColor4, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "취소")
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(Date(selectedDateMillis))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = blueColor4, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTime (
    showTimePicker : Boolean,
    context : Context,
    onClickConfirm: (String) -> Unit
) {
    var isShowTimePicker by remember {
        mutableStateOf(showTimePicker)
    }
    val selectedDate by remember { mutableStateOf<String?>(LocalDate.now().toString()) }

    if (isShowTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                selectedDate?.let { date ->
                    val selectedDateTime = "$date ${String.format("%02d:%02d", hour, minute)}"
                    onClickConfirm(selectedDateTime)
                }
                isShowTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
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
