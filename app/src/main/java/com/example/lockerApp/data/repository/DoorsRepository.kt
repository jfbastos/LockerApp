package com.example.lockerApp.data.repository

import com.example.lockerApp.data.model.DoorsResponse
import com.example.lockerApp.data.rest.ApiInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DoorsRepository(private val service : ApiInterface,private val dispatcher: CoroutineDispatcher) {


    suspend fun getDoors(page: Int, size: Int = 20): Result<DoorsResponse> = withContext(dispatcher){
        return@withContext runCatching { service.getDoors(page, size) }
    }
}