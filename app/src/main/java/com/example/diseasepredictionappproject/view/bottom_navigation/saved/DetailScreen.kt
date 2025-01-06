package com.example.diseasepredictionappproject.view.bottom_navigation.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.room_db.PredictionEntity
import com.example.diseasepredictionappproject.view_model.PredictionViewModel

@Composable
fun DetailScreen(
    id : Long?,
    navController: NavController,
    viewModel: PredictionViewModel = hiltViewModel()
) {
    LaunchedEffect(id) {
        viewModel.setSelectedId(id)
    }

    val selectData by viewModel.selectedSavedItem.collectAsState()

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
                    Icon(Icons.Default.ArrowBack, contentDescription = "backScreen")
                }
            }
        }


        DetailItem(selectData)
    }
}

@Composable
fun DetailItem(
    item : PredictionEntity?
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
    ) {
        Text(
            text = item?.diseaseName.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(text = item?.diseaseContent.toString())
    }
}