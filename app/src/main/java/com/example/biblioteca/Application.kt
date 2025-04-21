package com.example.biblioteca

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.biblioteca.appwrite.AppwriteClient

class BibliotecaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("is_dark_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}