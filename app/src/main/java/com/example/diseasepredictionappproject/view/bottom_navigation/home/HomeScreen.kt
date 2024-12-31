package com.example.diseasepredictionappproject.view.bottom_navigation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen( //건강관련팁, 질병정보검색, 최근예측결과요약
    navController : NavController
) {
    var isSearchOpen by remember {
        mutableStateOf(false)
    }

    //검색창으로 이동
    /*LaunchedEffect(isSearchOpen) {
        navController
    }*/

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(text = "질병 예측")
            
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                isSearchOpen = !isSearchOpen
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
        
        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            //최근 예측결과 요약

            //건강관련팁

            //북마크 데이터 보여주기
        }
    }
}