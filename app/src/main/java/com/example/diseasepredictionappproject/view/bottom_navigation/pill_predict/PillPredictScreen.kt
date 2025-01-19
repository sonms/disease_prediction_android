package com.example.diseasepredictionappproject.view.bottom_navigation.pill_predict

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.diseasepredictionappproject.R
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.ui.theme.blueColor5
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PillPredictScreen(
    navController: NavController
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

    var isImageSelected by remember {
        mutableStateOf(false)
    }

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
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
    }
}

@Composable
fun btnUI(
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

    btnUI(image = value, index = index) { click ->
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
