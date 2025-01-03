package com.example.diseasepredictionappproject

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.diseasepredictionappproject.ui.theme.DiseasePredictionAppProjectTheme
import com.example.diseasepredictionappproject.ui.theme.blueColor5
import com.example.diseasepredictionappproject.ui.theme.blueColor7
import com.example.diseasepredictionappproject.view.bottom_navigation.home.HomeScreen
import com.example.diseasepredictionappproject.view.bottom_navigation.predict.PredictScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    sealed class BottomNavItem(
        val title: Int, val icon: Int, val screenRoute: String
    ) {
        data object Prediction : BottomNavItem(R.string.bottom_prediction, R.drawable.prediction_vector_icon, "prediction")
        data object Saved : BottomNavItem(R.string.bottom_saved, R.drawable.baseline_bookmark_24, "bookmark")
        data object Home : BottomNavItem(R.string.bottom_home, R.drawable.baseline_home_24, "home")
        data object Settings : BottomNavItem(R.string.bottom_settings, R.drawable.baseline_settings_24, "settings")
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainContent() {
    val navController = rememberNavController()
    // 현재 라우트를 가져옵니다.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        /*floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            // 특정 라우트에서는 FloatingActionButton 숨깁니다.
            if (currentRoute == MainActivity.BottomNavItem.Home.screenRoute || currentRoute == MainActivity.BottomNavItem.Chart.screenRoute) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("edit_financial")
                    },
                    modifier = Modifier.padding(end = 10.dp),
                ) {
                    Icon(Icons.Default.Create, contentDescription = "CreateFinancialData")
                }
            }
        },*/
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
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        MainActivity.BottomNavItem.Home,
        MainActivity.BottomNavItem.Prediction,
        MainActivity.BottomNavItem.Saved,
        MainActivity.BottomNavItem.Settings,
    )

    androidx.compose.material3.NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val context = LocalContext.current
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title.toString(),
                        modifier = Modifier.wrapContentSize(),
                    )
                },
                label = {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = context.getString(item.title),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = blueColor7,
                    //indicatorColor = MaterialTheme.colorScheme.background,
                    selectedTextColor = blueColor5
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = MainActivity.BottomNavItem.Home.screenRoute) {
        composable(MainActivity.BottomNavItem.Home.screenRoute) {
            HomeScreen(navController)
        }
        composable(MainActivity.BottomNavItem.Prediction.screenRoute) {
            PredictScreen()
        }
        composable(MainActivity.BottomNavItem.Saved.screenRoute) {
            //CalendarScreen(navController)
        }
        composable(MainActivity.BottomNavItem.Settings.screenRoute) {
            //SettingScreen()
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun preView() {
    DiseasePredictionAppProjectTheme {
        MainContent()
    }
}