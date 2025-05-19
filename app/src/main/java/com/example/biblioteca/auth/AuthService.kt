package com.example.biblioteca.auth

import android.content.Context
import com.example.biblioteca.utils.PreferencesManager
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
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
            prefs.saveLoginState(true)
            prefs.saveRememberMe(rememberMe)

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            account.create(io.appwrite.ID.unique(), email, password)
            true
        } catch (e: Exception) {
            //  Imprime el error en Logcat    e.printStackTrace()
            false
        }
    }

    suspend fun logout(): Boolean {
        return try {
            account.deleteSession("current")
            true
        } catch (e: AppwriteException) {
            false
        }
    }
}
