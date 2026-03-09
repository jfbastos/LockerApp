package com.example.lockerApp.data.model

data class Door (
    val id: Long,
    val serial: String,
    val lockMAC: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val battery: Long
)
