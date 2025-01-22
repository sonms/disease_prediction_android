package com.example.diseasepredictionappproject.view.bottom_navigation.pill_predict

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.diseasepredictionappproject.R
import com.example.diseasepredictionappproject.data.PillInfoDataRequest
import com.example.diseasepredictionappproject.loading.LoadingState
import com.example.diseasepredictionappproject.room_db.medicine.MedicineEntity
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.ui.theme.blueColor5
import com.example.diseasepredictionappproject.view_model.FastApiViewModel
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import com.example.diseasepredictionappproject.view_model.PredictionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PillPredictScreen(
    navController: NavController,
    fastApiViewModel: FastApiViewModel = hiltViewModel(),
    viewModel : MedicineViewModel = hiltViewModel()
) {
    var hasPermissions by remember { mutableStateOf(false) }

    val permissions = listOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES
    )

    val permissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    hasPermissions = when {
        permissionState.allPermissionsGranted -> {
            true
        }
        permissionState.shouldShowRationale -> {
            false
        }
        else -> {
            false
        }
    }

    val uiState by fastApiViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var isImageSelected by remember {
        mutableStateOf(false)
    }

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    var predictionPillName by remember {
        mutableStateOf<String?>(null)
    }

    var isSaved by remember { mutableStateOf(false) }

    //통신제어
    var isComplete by remember {
        mutableStateOf(false)
    }
    //애니 뷰 제어
    var isVisible by remember { mutableStateOf(false) }
    var isAnotherTextVisible by remember {
        mutableStateOf(false)
    }
    var isTextVisible by remember {
        mutableStateOf(false)
    }

    //초기화
    LaunchedEffect(Unit) {
        selectedImage = null
        isImageSelected = false
        predictionPillName = null
        isComplete = false
        isVisible = false
        isAnotherTextVisible = false
        isSaved = false
        isTextVisible = false
        LoadingState.hide()
    }

    LaunchedEffect(predictionPillName) {
        if (predictionPillName != null) {
            delay(1000)
            isVisible = true
        }
    }

    when (uiState) {
        is FastApiViewModel.UiState.Loading -> {
            CoroutineScope(Dispatchers.IO).launch {
                LoadingState.show()
                try {
                    // 비동기 작업 수행
                } finally {
                    LoadingState.hide()
                }
            }
        }
        is FastApiViewModel.UiState.PillPrediction -> {
            val successData = (uiState as FastApiViewModel.UiState.PillPrediction).data
            try {
                Log.e("success", successData)
                predictionPillName = successData
                fastApiViewModel.fetchUIState(FastApiViewModel.UiState.Wait)
                isComplete = true
                LoadingState.hide()
            } catch (e : Exception) {
                fastApiViewModel.fetchUIState(FastApiViewModel.UiState.Wait)
            }
        }

        is FastApiViewModel.UiState.PillInfo -> {
            try {
                val successData = (uiState as FastApiViewModel.UiState.PillInfo).data
                Log.e("success", successData)
                /*val pillInfo = (uiState as FastApiViewModel.UiState.Success).data
                Log.d("issuccess", pillName)*/
                // diseaseName을 UI에 반영하거나, 필요한 작업을 추가로 수행
                val temp = MedicineEntity(
                    isBookMark = false,
                    entpName = successData.split("/").firstOrNull().orEmpty(),
                    itemName = predictionPillName.orEmpty(),
                    efcyQesitm = successData.split("/").getOrNull(1),
                    openDe = successData.split("/").getOrNull(2),
                    itemSeq = null,
                    useMethodQesitm = null,
                    atpnQesitm = null,
                    intrcQesitm = null,
                    seQesitm = null,
                    depositMethodQesitm = null,
                    updateDe = null,
                    itemImage = null,
                    bizrno = null,
                    atpnWarnQesitm = null
                )

                viewModel.addPredictionData(temp)

                fastApiViewModel.fetchUIState(FastApiViewModel.UiState.Wait)

                LoadingState.hide()
            } catch (e : Exception) {
                fastApiViewModel.fetchUIState(FastApiViewModel.UiState.Wait)
            }
        }

        is FastApiViewModel.UiState.Error -> {
            val error = (uiState as FastApiViewModel.UiState.Error).message
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
        is FastApiViewModel.UiState.Wait -> {

        }

        else -> {}
    }

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
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "backScreen")
                }
            }
        }


        Column (
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box (
                modifier = Modifier
                    .background(Color.LightGray, RoundedCornerShape(24.dp))
                    .aspectRatio(16f / 9f)
                    .fillMaxWidth(0.8f)
                    .weight(1f)
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                if (isImageSelected && selectedImage != null) {
                    selectedImage?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                } else {
                    Row {
                        Icon(painter = painterResource(id = R.drawable.baseline_image_24), contentDescription = "camera")

                        Text(text = "예측할 이미지")
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            if (selectedImage != null) {
                if (isComplete) {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)) +
                                slideInVertically(initialOffsetY = { -20 })
                    ) {
                        Text(
                            text = predictionPillName ?: "",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    LaunchedEffect(isVisible) {
                        if (isVisible) {
                            delay(2000) // AnimatedVisibility 애니메이션 지속 시간 (500ms) 이후 추가 딜레이
                            isTextVisible = true
                        }
                    }

                    if (isTextVisible) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(10.dp)
                                .alpha(
                                    if (isVisible) 1f else 0f
                                )
                            ,
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "저장하시겠습니까?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                            )

                            IconButton(
                                modifier = Modifier.padding(top = 16.dp),
                                onClick = {
                                    isSaved = !isSaved
                                    if (isSaved) {
                                        fastApiViewModel.fetchPillInfo(PillInfoDataRequest(predictionPillName ?: ""))
                                    }
                                }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isSaved) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24),
                                    contentDescription = "saved",
                                    tint = if (isSaved) blueColor5 else Color.Black
                                )
                            }
                        }
                    }
                } else {
                    BtnUI(image = R.drawable.baseline_send_24, index = 2, onClick = {
                        LoadingState.show()
                        fastApiViewModel.fetchPillPrediction(selectedImage!!, context)
                    })
                }
            }

            if (hasPermissions) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    //horizontalArrangement = Arrangement.spacedBy(8.dp) // 버튼 간 간격 설정
                ) {
                    // 카메라 버튼
                    OpenCameraOrAlbum(
                        value = R.drawable.baseline_photo_camera_24,
                        index = 0,
                        onImageSelected = { uri ->
                            isImageSelected = true
                            selectedImage = uri
                        }
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))

                    // 앨범 버튼
                    OpenCameraOrAlbum(
                        value = R.drawable.baseline_photo_album_24,
                        index = 1,
                        onImageSelected = { uri ->
                            isImageSelected = true
                            selectedImage = uri
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray, RoundedCornerShape(24.dp))
                        .wrapContentSize()
                        .padding(10.dp)
                        .weight(1f),
                        /*.aspectRatio(16f / 9f)
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight(),*/
                    contentAlignment = Alignment.Center
                ) {
                    Row {
                        Icon(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(end = 5.dp, start = 10.dp),
                            painter = painterResource(id = R.drawable.baseline_warning_24),
                            tint = Color.Yellow,
                            contentDescription = "warning message"
                        )

                        Text(
                            text = "설정에서 카메라와 갤러리 접근 권한을 허용해주세요.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BtnUI(
    image : Int,
    index : Int,
    onClick : (Int) -> Unit
) {
    Button(
        modifier = Modifier.wrapContentSize(),
        colors = ButtonDefaults.buttonColors(
            containerColor = blueColor5,
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = {
            onClick(index)
        }
    ) {
        Icon(painter = painterResource(id = image), contentDescription = "button icon")
        
        when (index) {
            0 -> Text(text = "카메라로 촬영")
            1 -> Text(text = "앨범에서 선택")
            2 -> Text(text = "예측 시작")
        }
    }
}

/*@Composable
fun ButtonRow(
    image : Int,
    index : Int,
    onClick : (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp) // 버튼 간 간격
    ) {
        btnUI(image = image, index = 0, onClick = { onClick(index) })
        btnUI(image = image, index = 1, onClick = { onClick(index) })
    }
}*/


@Composable
fun OpenCameraOrAlbum(
    value : Int,
    index : Int,
    onImageSelected : (Uri?) -> Unit
) {
    val context = LocalContext.current
    val launcherCamera = rememberLauncherForActivityResult(
        //takepicturepreview = 결과저장x -> 비트맵반환 / takepicture은 결과 uri 저장
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = {
            it?.let {
                val uri = saveBitmapToUri(context, it)
                onImageSelected(uri)
            }
        }
    )

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onImageSelected(uri)
    }

    BtnUI(image = value, index = index) { click ->
        when (click) {
            0 -> launcherCamera.launch(null) // 카메라 실행
            1 -> launcherGallery.launch("image/*") // 갤러리 실행
        }
    }
}

// Bitmap을 임시 파일로 저장 후 Uri를 반환
fun saveBitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

/*
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
) {
    val permissions = listOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    val permissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    when {
        permissionState.allPermissionsGranted -> onPermissionsGranted()
        permissionState.shouldShowRationale -> onPermissionsDenied()
        else -> onPermissionsDenied()
    }
}
*/
