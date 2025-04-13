package com.example.biblioteca.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    fun saveLoginState(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    fun saveRememberMe(remember: Boolean) {
        sharedPreferences.edit().putBoolean("remember_me", remember).apply()
    }

    fun isRemembered(): Boolean {
        return sharedPreferences.getBoolean("remember_me", false)
    }

    fun clearLoginState() {
        sharedPreferences.edit().clear().apply()
    }
}
