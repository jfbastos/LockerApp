package com.example.lockerApp.data.rest

import okhttp3.Interceptor
import okhttp3.Response

object AuthInterceptor : Interceptor{

    var sessionToken : String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request = request.newBuilder().addHeader("Authorization", "Bearer $sessionToken" ).build()

        return chain.proceed(request)
    }
}