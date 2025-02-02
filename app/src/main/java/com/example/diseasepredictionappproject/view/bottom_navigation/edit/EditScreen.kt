package com.example.diseasepredictionappproject.view.bottom_navigation.edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.diseasepredictionappproject.MainActivity
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.ui.theme.blueColor6
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditScreen(
    navController: NavController,
    type : String,
    id : String,
    predictionViewModel: PredictionViewModel = hiltViewModel(),
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val editPredictionData by predictionViewModel.selectedSavedItem.collectAsState()
    val editMedicineData by medicineViewModel.selectedSavedItem.collectAsState()
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)


    val isError by remember {
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

    if (type == "edit") {
        LaunchedEffect(Unit) {
            predictionViewModel.setSelectedId(id = id.toLongOrNull())
            medicineViewModel.setSelectedId(id = id.toLongOrNull())

            editType = if (editPredictionData != null) {
                "Prediction"
            } else if (editMedicineData != null) {
                "Medicine"
            } else {
                ""
            }
        }
    }




    Column (
        modifier = Modifier.fillMaxSize()
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = "backScreen")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                if (editType == "Prediction") {
                    predictionViewModel.addPredictionData(
                        editTitle,
                        editContent,
                        false,
                        ""
                    )
                } else if (editType == "Medicine") {
                    medicineViewModel.addPredictionData(
                        MedicineEntity(
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
                            updateDe = "",
                            itemImage = "",
                            bizrno = "",
                            isBookMark = false
                        )
                    )
                }

                navController.currentDestination?.route?.let {
                    navController.navigate(
                        it,
                        NavOptions.Builder()
                            .setLaunchSingleTop(true)  // 이미 존재하는 화면을 재사용하지 않음
                            .setPopUpTo(navController.graph.startDestinationId, true)  // 이전 화면을 스택에서 제거
                            .build()
                    )
                }
            }) {
                Icon(Icons.Default.Check, contentDescription = "edit complete")
            }
        }


        //제목
        EditTitle(
            textTitle = editTitle,
            onTextChange = {
                editTitle = it
            },
            isError = isError
        )

        EditType(
            fontSize,
            onClickType = {
                editType = it
            }
        )

        EditContent(
            editContent,
            onTextChange = {
                editContent = it
            }
        )
    }
}

@Composable
fun EditTitle(
    textTitle: String,
    onTextChange: (String) -> Unit,
    isError : Boolean
) {
    val textColor = MaterialTheme.colorScheme.primary // 동적으로 색상변경
    val isErrorCheck by rememberSaveable { mutableStateOf(isError) }
    /*var isError by rememberSaveable { mutableStateOf(false) }

    fun validate(text: String?) {
        isError = text.isNullOrEmpty()
    }*/

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
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
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            isError = isErrorCheck,
            /*trailingIcon = {
                if (isError)
                    Icon(Icons.Filled.Info, "빈 칸을 채워주세요!", tint = MaterialTheme.colorScheme.error)
            },*/
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = blueColor4
            ),
            label = {
                Text (
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "이름",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            },
            supportingText = {
                if (isError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "이름을 입력하세요.",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
        )
    }
}

@Composable
fun EditType (
    fontSize: FontSize,
    onClickType : (String) -> Unit
) {
    var selectedType by remember { mutableStateOf<String?>(null) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        // 질병 버튼
        Button(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                selectedType = "Prediction" // 선택된 타입을 업데이트
                onClickType("Prediction")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedType == "Prediction") blueColor4 else Color.LightGray
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

        Spacer(modifier = Modifier.weight(1f))

        // 약 버튼
        Button(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                selectedType = "Medicine" // 선택된 타입을 업데이트
                onClickType("Medicine")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedType == "Medicine") blueColor4 else Color.LightGray
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
}

@Composable
fun EditContent(
    textContent: String,
    onTextChange: (String) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.primary//동적으로 색상변경

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = textContent,
            onValueChange = onTextChange,
            singleLine = false,
            textStyle = TextStyle (
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = blueColor6
            ),
            label = {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "필요한 내용을 적어주세요",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Blue.copy(alpha = 0.5f)
                )
            }
        )
    }
}