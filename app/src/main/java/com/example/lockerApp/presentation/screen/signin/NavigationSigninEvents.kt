package com.example.lockerApp.presentation.screen.signin

sealed class NavigationSigninEvents {

    data object GoToSignUpScreen : NavigationSigninEvents()
    data object GoToHomeScreen : NavigationSigninEvents()

}