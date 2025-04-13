package com.example.biblioteca.auth

import android.content.Context
import com.example.biblioteca.utils.PreferencesManager
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.services.Account

class AuthService(private val context: Context) {

    private val client = Client()
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject("67f9710b0009513a166d")
        .setSelfSigned(true)

    private val account = Account(client)

    suspend fun login(email: String, password: String, rememberMe: Boolean): Boolean {
        return try {
            account.createEmailPasswordSession(email, password)

            val prefs = PreferencesManager(context)
            prefs.saveLoginState(true)                  // Guardamos que inició sesión
            prefs.saveRememberMe(rememberMe)            // Guardamos si quiere recordar o no

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            account.create(ID.unique(), email, password)
            true
        } catch (e: Exception) {
            false
        }
    }
}

