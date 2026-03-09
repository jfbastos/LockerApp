package com.example.lockerApp.presentation.screen.signup

import com.example.lockerApp.data.model.User

sealed class SignupIntents {

    data class OnSubmit(val user : User) : SignupIntents()

}