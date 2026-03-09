package com.example.lockerApp.presentation.screen.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockerApp.data.model.User
import com.example.lockerApp.data.repository.AuthenticationRepository
import com.example.lockerApp.data.repository.Either
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val authenticationRepository : AuthenticationRepository) : ViewModel(){

    private val _onSignupSuccess = MutableSharedFlow<String>(0)
    val onSignupSuccess = _onSignupSuccess

    private val _onError = MutableSharedFlow<String>(0)
    val onError = _onError

    fun onIntent(intent : SignupIntents) = viewModelScope.launch {
        when(intent){
            is SignupIntents.OnSubmit -> { submitUserInfo(intent.user) }
        }
    }

    fun submitUserInfo(user : User) = viewModelScope.launch{
        when(val result = authenticationRepository.validateSignup(user)){
            is Either.Left<String> -> { _onError.emit(result.value) }
            is Either.Right<String> -> { _onSignupSuccess.emit(result.value) }
        }
    }
}