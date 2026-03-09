package com.example.lockerApp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockerApp.data.repository.AuthenticationRepository
import com.example.lockerApp.data.repository.Either
import com.example.lockerApp.presentation.screen.signin.NavigationSigninEvents
import com.example.lockerApp.presentation.screen.signin.SigninIntents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SigninViewModel(private val authenticationRepository: AuthenticationRepository) : ViewModel(){

    private val _onNavigateEvent = MutableSharedFlow<NavigationSigninEvents>(0)
    val onNavigateEvent = _onNavigateEvent

    private val _onSigninSuccess = MutableSharedFlow<Unit>(1)
    val onSigninSuccess = _onSigninSuccess

    private val _onError = MutableSharedFlow<String>(0)
    val onError = _onError

    init {
        checkUserLogged()
    }

    fun onIntent(intent : SigninIntents) = viewModelScope.launch{
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
            Log.d("DEBUG", "User logged")
            _onSigninSuccess.emit(Unit)
        }else{
            Log.d("DEBUG", "User not logged")
        }
    }

    fun onSubmitSignin(email : String, password : String) = viewModelScope.launch {
        when(val result = authenticationRepository.validateSignin(email, password)){
            is Either.Left<String> -> { _onError.emit(result.value) }
            is Either.Right<*> -> { _onSigninSuccess.emit(Unit) }
        }
    }
}