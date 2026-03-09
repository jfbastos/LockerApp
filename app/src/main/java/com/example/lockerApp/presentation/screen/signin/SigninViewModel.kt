package com.example.lockerApp.presentation.screen.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockerApp.data.repository.AuthenticationRepository
import com.example.lockerApp.data.repository.Either
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SigninUiState{
    data object Loading : SigninUiState()
    data class OnSignIn(var mail : String) : SigninUiState()
    data object UserLogged : SigninUiState()
}


class SigninViewModel(private val authenticationRepository: AuthenticationRepository) : ViewModel(){

    private val _signinState : MutableStateFlow<SigninUiState> = MutableStateFlow(SigninUiState.Loading)
    val signinState = _signinState

    private val _onNavigateEvent = MutableSharedFlow<NavigationSigninEvents>(0)
    val onNavigateEvent = _onNavigateEvent

    private val _onError = MutableSharedFlow<String>(0)
    val onError = _onError

    init {
        checkUserLogged()
    }

    fun onIntent(intent : SigninIntents) = viewModelScope.launch {
        when(intent){
            SigninIntents.OnNavigateToSignUp -> { _onNavigateEvent.emit(
                NavigationSigninEvents.GoToSignUpScreen)
            }
            is SigninIntents.OnSubmitSignIn -> { onSubmitSignin(intent.email, intent.password)}
            SigninIntents.OnNavigateToHomeScreen -> {
                _onNavigateEvent.emit(NavigationSigninEvents.GoToHomeScreen)
            }
        }
    }

    fun checkUserLogged() = viewModelScope.launch {
        if(authenticationRepository.isUserAlreadyLogged()){
            _signinState.update { SigninUiState.UserLogged }
        }else{
            _signinState.update { SigninUiState.OnSignIn(authenticationRepository.loggedUser?.email ?: "") }
        }
    }

    fun onSubmitSignin(email : String, password : String) = viewModelScope.launch {
        when(val result = authenticationRepository.validateSignin(email, password)){
            is Either.Left<String> -> { _onError.emit(result.value) }
            is Either.Right<*> -> { _signinState.update { SigninUiState.UserLogged } }
        }
    }
}