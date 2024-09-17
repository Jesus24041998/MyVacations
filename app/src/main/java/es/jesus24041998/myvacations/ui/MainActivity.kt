package es.jesus24041998.myvacations.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import dagger.hilt.android.AndroidEntryPoint
import es.jesus24041998.myvacations.login.LoginScreen
import es.jesus24041998.myvacations.ui.home.HomeScreen
import es.jesus24041998.myvacations.ui.politics.Politics
import es.jesus24041998.myvacations.ui.splash.SplashScreen
import es.jesus24041998.myvacations.ui.theme.MyVacationsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyVacationsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    OpenNavigation()
                }
            }
        }
    }
}

@Composable
private fun OpenNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "SplashScreen") {
        composable("SplashScreen") {
            SplashScreen(
                onNavigateToHome = {
                    navigateToHome("SplashScreen", navController)
                })
        }
        composable("LoginScreen") {
            LoginScreen(onNavigateToHome = {
                navigateToHome("LoginScreen", navController)
            })
        }
        composable(
            "HomeScreen" + "/{fromSplash}",
            arguments = listOf(navArgument("fromSplash") { type = NavType.BoolType })
        ) { backStackEntry ->
            HomeScreen(
                fromSplash = backStackEntry.arguments?.getBoolean("fromSplash") ?: false,
                onNavigateToLogin = {
                    navigateToLogin("HomeScreen", navController)
                },
                onNavigateToPolitics = {
                    navigateToPolitics("HomeScreen", navController)
                })
        }
        composable("PoliticScreen") {
           Politics(navController = navController)
        }
    }
}

private fun navigateToLogin(window: String, navController: NavHostController) {
    navController.navigate(
        route = "LoginScreen",
        navOptions {
            anim {
                enter = android.R.animator.fade_in
                exit = android.R.animator.fade_out
                popEnter = android.R.animator.fade_in
                popExit = android.R.animator.fade_out
            }
            popUpTo(window) { inclusive = true }
        })
}

private fun navigateToHome(window: String, navController: NavHostController) {
    val route = if(window == "SplashScreen") "HomeScreen/"+true else "HomeScreen/"+false
    navController.navigate(
        route = route,
        navOptions {
            anim {
                enter = android.R.animator.fade_in
                exit = android.R.animator.fade_out
                popEnter = android.R.animator.fade_in
                popExit = android.R.animator.fade_out
            }
            popUpTo(window) { inclusive = true }
        })
}

private fun navigateToPolitics(window: String, navController: NavHostController) {
    navController.navigate(
        route = "PoliticScreen",
        navOptions {
            anim {
                enter = android.R.animator.fade_in
                exit = android.R.animator.fade_out
                popEnter = android.R.animator.fade_in
                popExit = android.R.animator.fade_out
            }
            popUpTo(window) { inclusive = true }
        })
}



