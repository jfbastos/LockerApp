package com.example.lockerApp.presentation.screen.signin

sealed class SigninIntents {

    data object OnNavigateToSignUp : SigninIntents()
    data object OnNavigateToHomeScreen : SigninIntents()
    data class OnSubmitSignIn(val email : String, val password : String) : SigninIntents()

}