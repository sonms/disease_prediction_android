package com.example.diseasepredictionappproject.view.bottom_navigation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diseasepredictionappproject.R

@Composable
fun SearchScreen(
    navController: NavController
) {
    var searchValue by remember {
        mutableStateOf("")
    }

    Column (
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
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp) // 아이콘 크기 조정
                )
            }

            /*BasicTextField(
                decorationBox = { innerTextField ->
                    TextFieldDefaults.TextFieldDecorationBox(
                        value = searchValue,
                        innerTextField = innerTextField,
                        visualTransformation = VisualTransformation.None,
                        enabled = true,
                        singleLine = 1,
                        placeholder = {
                            Placeholder(placeholderText = "placeholderText")
                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = searchValue.isNotEmpty(),
                                enter = fadeIn(),
                                exit = fadeOut(),
                            ) {
                                DeleteSearchQueryIcon(
                                    onClear = {
                                        searchValue = ""
                                    }
                                )
                            }
                        },
                    )
                },
            )*/
        }
    }



}

@Composable
fun DeleteSearchQueryIcon(
    onClear : () -> Unit
) {
    IconButton(onClick = { onClear() }) {
        Icon(Icons.Filled.Clear, contentDescription = "clearText")
    }
}