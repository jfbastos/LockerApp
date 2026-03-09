package com.example.lockerApp

import android.app.Application
import com.example.lockerApp.di.dispatcherModule
import com.example.lockerApp.di.repositoriesModule
import com.example.lockerApp.di.retrofitModule
import com.example.lockerApp.di.sharedPreferencesModule
import com.example.lockerApp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(listOf(dispatcherModule, retrofitModule, viewModelModule, repositoriesModule, sharedPreferencesModule))
        }
    }

}