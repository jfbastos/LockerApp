package com.example.lockerApp.presentation.screen.home

sealed class DoorsIntents {

    data object OnLoadMoreDoors : DoorsIntents()
    data object OnReloadDoors : DoorsIntents()
    data class OnSearchDoors(val query : String) : DoorsIntents()

}