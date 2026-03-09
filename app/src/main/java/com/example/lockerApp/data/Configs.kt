package com.example.lockerApp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class Configs(context : Context) {

    private var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var token : String?
        get() = sharedPreferences.getString("TOKEN_KEY", "")
        set(value) { sharedPreferences.edit{ putString("TOKEN_KEY", value)} }

}