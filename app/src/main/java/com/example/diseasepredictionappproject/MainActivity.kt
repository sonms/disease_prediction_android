package com.example.diseasepredictionappproject

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diseasepredictionappproject.loading.GlobalLoadingScreen
import com.example.diseasepredictionappproject.ui.theme.DiseasePredictionAppProjectTheme
import com.example.diseasepredictionappproject.ui.theme.blueColor4
import com.example.diseasepredictionappproject.ui.theme.blueColor5
import com.example.diseasepredictionappproject.ui.theme.blueColor7
import com.example.diseasepredictionappproject.utils.FontSize
import com.example.diseasepredictionappproject.utils.FontUtils
import com.example.diseasepredictionappproject.utils.PreferenceDataStore
import com.example.diseasepredictionappproject.view.bottom_navigation.edit.EditScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.home.HomeScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.home.SearchScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.pill_predict.PillPredictScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.predict.PredictScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.DetailScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.SavedScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.saved.result.ResultScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.settings.SettingsScreen
import com.example.diseasepredictionappproject.view_model.MedicineViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    sealed class BottomNavItem(
        val title: Int, val icon: Int, val screenRoute: String
    ) {
        data object Prediction : BottomNavItem(R.string.bottom_prediction, R.drawable.prediction_vector_icon, "prediction")
        data object Saved : BottomNavItem(R.string.bottom_saved, R.drawable.baseline_bookmark_border_24, "bookmark")
        data object Home : BottomNavItem(R.string.bottom_home, R.drawable.baseline_home_24, "home")
        data object Settings : BottomNavItem(R.string.bottom_settings, R.drawable.baseline_settings_24, "settings")
        data object PillPredictionCamera : BottomNavItem(R.string.bottom_center, R.drawable.baseline_camera_24, "pill")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiseasePredictionAppProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                    GlobalLoadingScreen()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainContent() {
    val navController = rememberNavController()
    // 현재 라우트를 가져옵니다.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val fontSize by PreferenceDataStore.getFontSizeFlow(context).collectAsState(initial = FontSize.Medium)
    var isPermissionState by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
           if (currentRoute in listOf("home","prediction","bookmark","settings")) {
               TopAppBar(navController = navController, fontSize = fontSize)
           }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
           if (currentRoute in listOf("home", "bookmark")) {
               EditFab {
                   navController.navigate("edit?type=add&data={data}&id={id}")
               }
           }
        },
        bottomBar = {
            // 특정 라우트에서는 BottomNavigation을 숨깁니다.
            if (currentRoute !in listOf(
                    "edit_financial?type={type}&id={id}",
                    "edit_financial"
                )
            ) {
                BottomNavigation(navController = navController)
            }
        },
    ) {
        Box(Modifier.padding(it)) {
            NavigationGraph(navController = navController)
        }
    }

    if (isPermissionState) {
        RequestNotificationPermission(
            context,
            fontSize,
            onClickBtn = {
                isPermissionState = false
            }
        )
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        MainActivity.BottomNavItem.Home,
        MainActivity.BottomNavItem.Prediction,
        MainActivity.BottomNavItem.PillPredictionCamera,
        MainActivity.BottomNavItem.Saved,
        MainActivity.BottomNavItem.Settings,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            val path = Path().apply {
                val width = size.width
                val height = size.height

                // Start point
                moveTo(0f, 0f)

                // Left flat section
                lineTo(width * 0.35f, 0f)

                // Upper curve for central button
                cubicTo(
                    width * 0.4f, 0f,
                    width * 0.45f, height * 0.3f,
                    width * 0.5f, height * 0.3f
                )
                cubicTo(
                    width * 0.55f, height * 0.3f,
                    width * 0.6f, 0f,
                    width * 0.65f, 0f
                )

                // Right flat section
                lineTo(width, 0f)

                // 바텀 경계선
                lineTo(width, height)
                lineTo(0f, height)

                close()
            }
            drawPath(
                path = path,
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            //간격의 시작 타이밍이 다름 - evenly는 맨앞부터 간격시작
            //between은 item부터
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEachIndexed { index, item ->
                if (index == 2) {
                    /*Column (
                        modifier = Modifier.navigationBarsPadding().padding(bottom = 5.dp),
                    ) {
                        Spacer(modifier = Modifier.weight(1f, true))
                        Text(
                            text = stringResource(id = R.string.bottom_center), // 버튼 아래 이름
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                        )
                    }*/
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(MainActivity.BottomNavItem.PillPredictionCamera.screenRoute) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                    launchSingleTop = true // 중복 스택 방지
                                    restoreState = true
                                }
                            },
                            containerColor = blueColor5,
                            modifier = Modifier
                                .wrapContentSize()
                                .offset(y = (-10).dp),
                            shape = RoundedCornerShape(36.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_camera_24),
                                contentDescription = "Center Button",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.bottom_center), // 버튼 아래 이름
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                } else {
                    NavigationBarItem(
                        //그림자 파장효과 제거
                        interactionSource = NoRippleInteractionSource,
                        selected = currentRoute == item.screenRoute,
                        onClick = {
                            /*navController.navigate(item.screenRoute) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                restoreState = true
                                launchSingleTop = true
                            }*/
                            navController.navigate(item.screenRoute) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true // 현재 스크린 제거
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(id = item.icon),
                                contentDescription = item.title.toString(),
                                modifier = Modifier.wrapContentSize()
                            )
                        },
                        label = {
                            Text(
                                text = LocalContext.current.getString(item.title),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = blueColor7,
                            selectedTextColor = blueColor5
                        )
                    )
                }
            }
        }

        // 가운데 버튼
        /*FloatingActionButton(
            onClick = {
                navController.navigate(MainActivity.BottomNavItem.PillPredictionCamera.screenRoute)
            },
            containerColor = blueColor5,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_camera_24),
                contentDescription = "Center Button",
                tint = Color.White
            )
        }*/
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    medicineViewModel: MedicineViewModel = hiltViewModel()
) {
    NavHost(navController = navController, startDestination = MainActivity.BottomNavItem.Home.screenRoute) {
        composable(MainActivity.BottomNavItem.Home.screenRoute) {
            HomeScreen(navController)
        }
        composable(MainActivity.BottomNavItem.Prediction.screenRoute) {
            PredictScreen()
        }
        composable(MainActivity.BottomNavItem.PillPredictionCamera.screenRoute) {
            PillPredictScreen(navController = navController)
        }
        composable(MainActivity.BottomNavItem.Saved.screenRoute) {
            //CalendarScreen(navController)
            SavedScreen(navController = navController, medicineViewModel = medicineViewModel)
        }
        composable(MainActivity.BottomNavItem.Settings.screenRoute) {
            SettingsScreen(navController = navController)
        }


        composable(
            route = "detail?id={id}&type={type}&data={data}",
            arguments = listOf(
                navArgument("id") { defaultValue = "-1" },
                navArgument("type") { defaultValue = "disease" },
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id") ?: "-1"
            val type = backStackEntry.arguments?.getString("type") ?: "disease"
            DetailScreen(id = id.toLongOrNull(), type = type, navController, medicineViewModel = medicineViewModel)
        }

        composable(
            route = "result",
            /*arguments = listOf(
                navArgument("drugInfo") { defaultValue = "" }
            )*/
        ) { _ ->

            //val drugInfo = backStackEntry.arguments?.getString("drugInfo") ?: ""
            ResultScreen(navController = navController,  medicineViewModel = medicineViewModel)
        }

        composable(
            route = "search",

        ) { _ ->
            SearchScreen(navController)
        }

        composable(
            route = "edit?type={type}&data={data}&id={id}",
            arguments = listOf(
                navArgument("type") { defaultValue = "default" },
                navArgument("data") {defaultValue = "default"},
                navArgument("id") { defaultValue = "-1" }
            )
        ) { backStackEntry ->

            val type = backStackEntry.arguments?.getString("type") ?: "default"
            val data = backStackEntry.arguments?.getString("data") ?:"default"
            val id = backStackEntry.arguments?.getString("id") ?: "-1"

            EditScreen(navController, type = type, data = data, id = id)
        }

        /*composable(
            route = "edit_financial?type={type}&id={id}",
            arguments = listOf(
                navArgument("type") { defaultValue = "default" },
                navArgument("id") { defaultValue = "-1" }
            )
        ) { backStackEntry ->

            val type = backStackEntry.arguments?.getString("type") ?: "default"
            val id = backStackEntry.arguments?.getString("id") ?: "-1"

            EditFinancialScreen(navController, type = type, id = id)
        }

        composable(
            route = "search?text={searchText}",
            arguments = listOf(
                navArgument("searchText") { defaultValue = "" } // 키 수정
            )
        ) { backStackEntry ->
            val searchText = backStackEntry.arguments?.getString("searchText") ?: ""
            Log.d("SearchScreen", "searchText received: $searchText")
            SearchScreen(navController, searchText)
        }*/
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController : NavController,
    fontSize : FontSize
) {
    androidx.compose.material3.TopAppBar(
        modifier = Modifier.wrapContentSize(),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title: what2c
                Text(
                    text = "질병 예측",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 5.dp),
                    style = FontUtils.getTextStyle(fontSize.size + 2f)
                )
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )

    /*androidx.compose.material3.TopAppBar (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "질병 예측", modifier = Modifier.padding(start = 10.dp), style = FontUtils.getTextStyle(fontSize.size + 4f))

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = {
            navController.navigate("search")
        }) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }*/
}

@Composable
fun EditFab (
    onClickFab : () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier.padding(end = 10.dp, bottom = 10.dp),
        contentColor = blueColor4,
        containerColor = blueColor7,
        shape = RoundedCornerShape(36.dp),
        onClick = { onClickFab() }
    ) {
        Icon(Icons.Default.Add, contentDescription = "edit data")
    }
}

@Composable
fun RequestNotificationPermission (
    context: Context,
    fontSize: FontSize,
    onClickBtn : (Boolean) -> Unit
) {
    var permissionState by remember { mutableStateOf(checkNotificationPermission(context)) }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            permissionState = isGranted
        }

    if (permissionState) {
        Toast.makeText(context, "알림 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        onClickBtn(true)
    } else {
        PermissionDialog(
            item = permissionState,
            fontSize = fontSize,
            onCancelClick = {
                Toast.makeText(context, "알림 권한이 비허용 되었습니다.", Toast.LENGTH_SHORT).show()
                onClickBtn(false)
            },
            onConfirmClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    onClickBtn(true)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
                    if (alarmManager?.canScheduleExactAlarms() == false) {
                        Intent().also { intent ->
                            intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                            context.startActivity(intent)
                        }
                        onClickBtn(true)
                    }
                }
            }
        )
    }
}

private fun checkNotificationPermission(context : Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

@Composable
fun PermissionDialog(
    item : Boolean,
    onConfirmClick : (Boolean) -> Unit,
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
                text = "알림 권한이 필요합니다",
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
                        containerColor = blueColor4,
                        contentColor = Color.White
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

/**
 * 클릭시 알약 파장 효과를 제거.
 */
private object NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}

    override fun tryEmit(interaction: Interaction) = true
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun preView() {
    DiseasePredictionAppProjectTheme {
        MainContent()
    }
}