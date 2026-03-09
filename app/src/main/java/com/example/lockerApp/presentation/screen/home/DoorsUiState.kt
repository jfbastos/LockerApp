package com.example.lockerApp.presentation.screen.home

import com.example.lockerApp.data.model.Door

data class DoorsUiState(
    val doors: List<Door> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasMore: Boolean = true
)