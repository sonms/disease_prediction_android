package com.example.diseasepredictionappproject.view.bottom_navigation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController
) {
    var isOpen by remember {
        mutableStateOf(false)
    }
    //언어 설정?

    //다크모드

    //문의 및 설문조사?

    //글자 크기 조정?
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)




    Column (
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                isOpen = true
            },
    ) {
        Text(
            text = "글자 크기 조정",
            style = FontUtils.getTextStyle(fontSize.size),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "이곳을 눌러 글자 크기 조절하기",
            style = FontUtils.getTextStyle(fontSize.size),
            modifier = Modifier
                .padding(top = 5.dp),
            fontWeight = FontWeight.SemiBold
        )
    }

    if (isOpen) {
        FontSizeSettingDialog (
            fontSize = fontSize,
            onCancelClick = {
                isOpen = !isOpen
            },
            onConfirmClick = {size ->
                when(size) {
                    "작게" -> {
                        isOpen = !isOpen
                        CoroutineScope(Dispatchers.IO).launch {
                            PreferenceDataStore.setFontSize(context, FontSize.Small)
                        }
                    }

                    "중간" -> {
                        isOpen = !isOpen
                        CoroutineScope(Dispatchers.IO).launch {
                            PreferenceDataStore.setFontSize(context, FontSize.Medium)
                        }
                    }

                    "크게" -> {
                        isOpen = !isOpen
                        CoroutineScope(Dispatchers.IO).launch {
                            PreferenceDataStore.setFontSize(context, FontSize.Large)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun FontSizeSettingDialog(
    onConfirmClick : (String?) -> Unit,
    onCancelClick : () -> Unit,
    fontSize: FontSize
) {
    Dialog(
        onDismissRequest = { onCancelClick() }
    ) {
        Card(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .padding(10.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(15.dp).clickable { onConfirmClick("작게") },
                text = "작게",
                fontWeight = FontWeight.SemiBold,
                style = FontUtils.getTextStyle(fontSize.size)
            )

            Text(
                modifier = Modifier.padding(15.dp).clickable { onConfirmClick("중간") },
                text = "중간",
                fontWeight = FontWeight.SemiBold,
                style = FontUtils.getTextStyle(fontSize.size + 2f)
            )

            Text(
                modifier = Modifier.padding(15.dp).clickable { onConfirmClick("크게") },
                text = "크게",
                fontWeight = FontWeight.SemiBold,
                style = FontUtils.getTextStyle(fontSize.size + 4f)
            )
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