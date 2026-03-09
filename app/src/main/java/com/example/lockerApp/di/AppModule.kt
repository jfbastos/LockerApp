package com.example.lockerApp.di

import com.example.lockerApp.data.Configs
import com.example.lockerApp.data.repository.AuthenticationRepository
import com.example.lockerApp.data.repository.DoorsRepository
import com.example.lockerApp.data.rest.ApiInterface
import com.example.lockerApp.data.rest.AuthInterceptor
import com.example.lockerApp.presentation.screen.signin.SigninViewModel
import com.example.lockerApp.presentation.screen.home.DoorsViewModel
import com.example.lockerApp.presentation.screen.signup.SignupViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val sharedPreferencesModule = module {
    single { Configs(androidContext()) }
}

val viewModelModule = module {
    viewModel { SignupViewModel(get()) }
    viewModel { SigninViewModel(get()) }
    viewModel { DoorsViewModel(get()) }
}

val repositoriesModule = module {
    single { AuthenticationRepository(get(), get() ,get(named("IODispatcher"))) }
    single { DoorsRepository(get(), get(named("IODispatcher"))) }
}

val dispatcherModule = module { single ( named("IODispatcher")) { Dispatchers.IO } }

val retrofitModule = module {
    single { provideRetrofit() }
    factory { provideApi(get()) }
}

fun provideRetrofit() : Retrofit = Retrofit.Builder()
    .baseUrl(ApiInterface.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create(gsonConverterFactory()))
    .client(okHttpClient())
    .build()


fun gsonConverterFactory() : Gson = GsonBuilder()
    .setStrictness(Strictness.LENIENT)
    .create()

fun provideApi(retrofit: Retrofit) : ApiInterface =
    retrofit.create(ApiInterface::class.java)

fun okHttpClient() : OkHttpClient{
    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor)
        .build()
}