package com.example.lockerApp.data.model

data class ValidationError(
    val code: String,
    val description: String,
    val fieldErrors: List<FieldError>
)

