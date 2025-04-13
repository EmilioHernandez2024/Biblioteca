package com.example.biblioteca

import android.app.Application
import com.example.biblioteca.appwrite.AppwriteClient

class BibliotecaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppwriteClient.init(this)
    }
}
