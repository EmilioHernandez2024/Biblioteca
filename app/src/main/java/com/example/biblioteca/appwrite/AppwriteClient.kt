package com.example.biblioteca.appwrite

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account

/**
 *informaciones clave de la base de datos AppWrite
 */

object AppwriteClient {
    lateinit var client: Client
    lateinit var account: Account

    fun init(context: Context) {
        client = Client(context.toString())
            .setEndpoint("https://cloud.appwrite.io/v1") // Usa Appwrite Cloud
            .setProject("67f9710b0009513a166d") // Coloca aquí tu ID de proyecto

        account = Account(client)
    }
}
