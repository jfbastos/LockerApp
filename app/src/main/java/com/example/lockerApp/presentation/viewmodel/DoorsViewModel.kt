package com.example.lockerApp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockerApp.data.model.Door
import com.example.lockerApp.data.repository.DoorsRepository
import com.example.lockerApp.presentation.screen.home.DoorsIntents
import com.example.lockerApp.presentation.screen.home.DoorsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DoorsViewModel(val doorsRepository: DoorsRepository) : ViewModel(){

    private val _uiState = MutableStateFlow(DoorsUiState())
    val uiState: StateFlow<DoorsUiState> = _uiState.asStateFlow()

    init {
        loadDoors()
    }

    fun onIntent(intent : DoorsIntents){
        when(intent){
            DoorsIntents.OnLoadMoreDoors -> loadMore()
            DoorsIntents.OnReloadDoors -> loadDoors()
        }
    }

    private fun loadDoors() =  viewModelScope.launch{
        _uiState.update { it.copy(isLoading = true, error = null) }

        doorsRepository.getDoors(page = 0)
            .onSuccess { response ->
                _uiState.update {
                    it.copy(
                        doors = response.content,
                        isLoading = false,
                        currentPage = 0,
                        totalPages = response.totalPages,
                        hasMore = response.page < response.totalPages - 1
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message)
                }
            }
    }

    private fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val nextPage = state.currentPage + 1

            doorsRepository.getDoors(page = nextPage)
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            doors = it.doors + response.content,
                            isLoadingMore = false,
                            currentPage = nextPage,
                            totalPages = response.totalPages,
                            hasMore = nextPage < response.totalPages - 1
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoadingMore = false, error = error.message)
                    }
                }
        }
    }



}