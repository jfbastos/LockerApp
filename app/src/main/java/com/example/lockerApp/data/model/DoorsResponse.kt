package com.example.lockerApp.data.model

data class DoorsResponse(
    val content: List<Door>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)