package com.example.biblioteca.auth

import android.content.Context
import com.example.biblioteca.utils.PreferencesManager
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account

class AuthService(private val context: Context) {

    /**
     * aqui es donde se sincroniza la autenticacion de la base de datos para los usuario
     * y de nuevo se vuelve a poner las claves de appwrite
     */


    private val client = Client()
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject("67f9710b0009513a166d")
        .setSelfSigned(true)

    private val account = Account(client)

    /**
     * Se manda la informacion de login para verificar si es la misma que esta en base de datos
     */

    suspend fun login(email: String, password: String, rememberMe: Boolean): Boolean {
        // Intenta crear una sesión de usuario con el email y la contraseña proporcionados.
        // 'suspend' indica que esta función es una corrutina y puede pausar su ejecución.
        return try {
            // Llama al método de la API para iniciar sesión.
            account.createEmailPasswordSession(email, password)

            // Si la sesión se crea con éxito, guarda el estado de inicio de sesión en las preferencias del usuario.
            val prefs = PreferencesManager(context) // Crea una instancia del gestor de preferencias.
            prefs.saveLoginState(true) // Guarda que el usuario ha iniciado sesión.
            prefs.saveRememberMe(rememberMe) // Guarda la preferencia de "recordarme".

            true // Retorna 'true' indicando que el inicio de sesión fue exitoso.
        } catch (e: Exception) {
            // Si ocurre alguna excepción durante el proceso (ej. credenciales incorrectas, error de red),
            // se captura aquí.
            false // Retorna 'false' indicando que el inicio de sesión falló.
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        // Intenta registrar un nuevo usuario con el email y la contraseña proporcionados.
        return try {
            // Usa `ID.unique()` para generar un ID de usuario único automáticamente.
            account.create(io.appwrite.ID.unique(), email, password)
            true // Retorna 'true' si el registro fue exitoso.
        } catch (e: Exception) {
            // Si ocurre un error durante el registro (ej. email ya registrado, contraseña débil).
            false // Retorna 'false' indicando que el registro falló.
        }
    }

    suspend fun logout(): Boolean {
        // Intenta cerrar la sesión actual del usuario.
        return try {
            account.deleteSession("current") // Elimina la sesión actualmente activa.
            true // Retorna 'true' si el cierre de sesión fue exitoso.
        } catch (e: AppwriteException) {
            // Captura errores específicos de Appwrite si el cierre de sesión falla (ej. no hay sesión activa).
            false // Retorna 'false' indicando que el cierre de sesión falló.
        }
    }
}
