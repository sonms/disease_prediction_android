package com.example.diseasepredictionappproject.view.bottom_navigation.saved

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.R
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.ui.theme.blueColor1
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.view_model.PredictionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SavedScreen(
    navController: NavController,
    viewModel : PredictionViewModel = hiltViewModel()
) {
    val allPredictionData by viewModel.allPredictionsData.collectAsState(initial = emptyList())
    var isShow by remember {
        mutableStateOf(false)
    }
    var deleteItem by remember {
        mutableStateOf<PredictionEntity?>(null)
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        LazyColumn (
            contentPadding = PaddingValues(vertical = 10.dp) // 각 항목 간 간격을 10dp로 설정
        ) {
            itemsIndexed(
                items = allPredictionData
            ) { _, item ->
                SavedItem(
                    item,
                    onClick = {
                        navController.navigate("detail?id=${item.id}")
                    },
                    onLongClick = {
                        deleteItem = it
                        isShow = !isShow
                    },
                    onStarClick = {
                        /*val bookMarkedData = PredictionEntity (
                            diseaseName = item.diseaseName,
                            diseaseContent = item.diseaseContent,
                            isBookMark = !item.isBookMark!!,
                            recommendMedication = ""
                        )*/

                        viewModel.updateOnlySpecificData(id = item.id, isBookMark = !item.isBookMark!!)
                    },
                )
            }
        }
    }

    if (isShow) {
        DeleteItemDialog(deleteItem,
            onConfirmClick = {
                isShow = !isShow
                it?.let {
                    viewModel.deletePredictionData(it)
                }
            },
            onCancelClick = {
                isShow = !isShow
            })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedItem(
    data : PredictionEntity,
    onClick : (PredictionEntity) -> Unit, // 정보확인용
    onLongClick : (PredictionEntity) -> Unit, //삭제용
    onStarClick : (Boolean) -> Unit
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
               Text(text = data.diseaseName.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)

               Spacer(modifier = Modifier.height(8.dp))

               Text(
                   text = "Date: ${data.createDate.split("T").first()}",
                   style = MaterialTheme.typography.bodySmall,
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

@Composable
fun DeleteItemDialog(
    item : PredictionEntity?,
    onConfirmClick : (PredictionEntity?) -> Unit,
    onCancelClick : () -> Unit
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
                fontSize = 18.sp,
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
                    Text(text = "취소")
                }

                Button(
                    onClick = { onConfirmClick(item) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueColor4, // 버튼 배경색
                        contentColor = Color.White // 텍스트 색상 설정
                    ),
                ) {
                    Text(text = "확인")
                }
            }
        }
    }
}