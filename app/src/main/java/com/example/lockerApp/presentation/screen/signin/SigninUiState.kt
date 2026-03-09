package com.example.lockerApp.presentation.screen.signin

sealed class SigninUiState{
    data object Loading : SigninUiState()
    data class OnSignIn(var mail : String) : SigninUiState()
    data object UserLogged : SigninUiState()
}