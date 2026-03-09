package com.example.lockerApp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lockerApp.presentation.screen.home.HomeScreenNavigation
import com.example.lockerApp.presentation.screen.signin.SigninScreenNavigation
import com.example.lockerApp.presentation.screen.signup.SignupScreenNavigation
import com.example.lockerApp.utils.Constants

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Constants.SIGNIN_SCREEN_DESTINATION
    ){
        composable(Constants.SIGNUP_SCREEN_DESTINATION){
            SignupScreenNavigation(navController)
        }
        composable(Constants.SIGNIN_SCREEN_DESTINATION){
            SigninScreenNavigation(navController)
        }
        composable(Constants.HOME_SCREEN_DESTINATION){
            HomeScreenNavigation(navController)
        }
    }

}