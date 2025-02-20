package com.example.diseasepredictionappproject.view.bottom_navigation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.BuildConfig
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

    //글자 크기 조정
    val context = LocalContext.current
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)


    //알림
    val isAlarmCheck by PreferenceDataStore.getAlarmFlow(context).collectAsState(initial = false)



    LazyColumn (
        modifier = Modifier.wrapContentHeight()
    ) {
        item {
            SettingDivideItem(mainText = "글자 크기 조정", subText = "이곳을 눌러 글자 크기 조절하기", fontSize = fontSize) {
                isOpen = !isOpen
            }
        }

        item {
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
                    checked = isAlarmCheck,
                    onCheckedChange = {
                        CoroutineScope(Dispatchers.IO).launch {
                            PreferenceDataStore.setAlarmEnabled(context, it)
                        }
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
        }

        item {
            SettingDivideItem(mainText = "문의하기", subText = "Gmail로 이메일 보내기", fontSize = fontSize) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.Inquire)) // 받는 사람
                    putExtra(Intent.EXTRA_SUBJECT, "문의 사항")
                }

                // Gmail 앱이 설치되어 있는 경우 우선 열기
                intent.setPackage("com.google.android.gm")

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    // Gmail이 설치되지 않은 경우 다른 이메일 앱으로 열기
                    val chooser = Intent.createChooser(intent, "이메일 앱을 선택하세요")
                    context.startActivity(chooser)
                }
            }
        }

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
                modifier = Modifier
                    .padding(15.dp)
                    .clickable { onConfirmClick("작게") },
                text = "작게",
                fontWeight = FontWeight.SemiBold,
                style = FontUtils.getTextStyle(fontSize.size)
            )

            Text(
                modifier = Modifier
                    .padding(15.dp)
                    .clickable { onConfirmClick("중간") },
                text = "중간",
                fontWeight = FontWeight.SemiBold,
                style = FontUtils.getTextStyle(fontSize.size + 2f)
            )

            Text(
                modifier = Modifier
                    .padding(15.dp)
                    .clickable { onConfirmClick("크게") },
                text = "크게",
                fontWeight = FontWeight.SemiBold,
                style = FontUtils.getTextStyle(fontSize.size + 4f)
            )
        }
    }
}



@Composable
fun SettingDivideItem(
    mainText : String,
    subText : String,
    fontSize : FontSize,
    onClickItem : () -> Unit
) {
    when(mainText) {
        "문의하기" -> {
            Column (
                modifier = Modifier
                    .clickable { onClickItem() }
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = mainText,
                    style = FontUtils.getTextStyle(fontSize.size),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subText,
                    style = FontUtils.getTextStyle(fontSize.size-2f),
                    modifier = Modifier
                        .padding(top = 5.dp),
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    thickness = 2.dp,
                    color = Color.Gray.copy(0.5f)
                )
            }
        }

        else -> {
            Column (
                modifier = Modifier
                    .clickable { onClickItem() }
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = mainText,
                    style = FontUtils.getTextStyle(fontSize.size),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subText,
                    style = FontUtils.getTextStyle(fontSize.size-2f),
                    modifier = Modifier
                        .padding(top = 5.dp),
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    thickness = 2.dp,
                    color = Color.Black.copy(0.5f)
                )
            }
        }

    }
}