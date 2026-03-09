package com.example.lockerApp.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockerApp.data.repository.DoorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DoorsViewModel(private val doorsRepository: DoorsRepository) : ViewModel(){

    private val _uiState = MutableStateFlow(DoorsUiState())
    val uiState: StateFlow<DoorsUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        loadDoors()

        viewModelScope.launch {
            searchQueryFlow
                .debounce(500L)
                .distinctUntilChanged()
                .collectLatest { loadDoors() }
        }
    }

    fun onIntent(intent : DoorsIntents){
        when(intent){
            DoorsIntents.OnLoadMoreDoors -> loadMore()
            DoorsIntents.OnReloadDoors -> loadDoors()
            is DoorsIntents.OnSearchDoors -> {
                _uiState.update { it.copy(searchQuery = intent.query) }
                searchQueryFlow.value = intent.query
            }
        }
    }

    private fun loadDoors() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val result = if (searchQueryFlow.value.isBlank()) {
            doorsRepository.getDoors(0)
        } else {
            doorsRepository.searchDoors(query = searchQueryFlow.value, page = 0)
        }

        result.onSuccess { response ->
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

            val result = if(searchQueryFlow.value.isBlank()){
                doorsRepository.getDoors(page = nextPage)
            }else{
                doorsRepository.searchDoors(query = searchQueryFlow.value, page = nextPage)
            }

            result.onSuccess { response ->
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