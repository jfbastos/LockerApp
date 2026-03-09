package com.example.lockerApp.data.rest

import com.example.lockerApp.data.model.DoorsResponse
import com.example.lockerApp.data.model.SiginResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface  ApiInterface {

    companion object{
        const val BASE_URL = "https://hiring-api.samba.dev.assaabloyglobalsolutions.net"
    }

    @POST("/users/signup")
    fun submitNewUser(@Body user : JsonObject) : Call<JsonObject?>?

    @POST("/users/signin")
    suspend fun submitSignin(@Body siginInfo : JsonObject) : Response<SiginResponse>

    @GET("doors")
    suspend fun getDoors(
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): DoorsResponse

    @GET("doors/find")
    suspend fun searchDoors(
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): DoorsResponse


}