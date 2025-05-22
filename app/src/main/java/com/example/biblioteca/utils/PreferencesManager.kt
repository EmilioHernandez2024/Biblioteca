package com.example.biblioteca.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * es la actividad que maneja el proceso de preferencia con respecto al modo noche o dia si se
 * guardo antes de salirse
 */

class PreferencesManager(context: Context) {

    // El modo privado significa que solo esta aplicación puede acceder a estas preferencias.
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    /**
     * Guarda el estado de inicio de sesión del usuario.
     */
    fun saveLoginState(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", isLoggedIn).apply()
        // `edit()` obtiene un editor para modificar las preferencias.
        // `putBoolean()` añade un valor booleano con la clave "is_logged_in".
        // `apply()` guarda los cambios de forma asíncrona en el disco.
    }

    /**
     * Comprueba si el usuario ha iniciado sesión.
     * @return `true` si el usuario ha iniciado sesión, `false` por defecto si no se encuentra la clave.
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    /**
     * Guarda la preferencia de "recordarme" del usuario.
     * @param remember `true` si el usuario quiere ser recordado, `false` en caso contrario.
     */
    fun saveRememberMe(remember: Boolean) {
        sharedPreferences.edit().putBoolean("remember_me", remember).apply()
    }

    /**
     * Comprueba si el usuario ha activado la opción "recordarme".
     * @return `true` si la opción "recordarme" está activada, `false` por defecto.
     */
    fun isRemembered(): Boolean {
        return sharedPreferences.getBoolean("remember_me", false)
    }

    /**
     * Borra todos los estados y preferencias guardados en "login_prefs".
     * Esto es útil para cerrar la sesión o restablecer las configuraciones de login.
     */
    fun clearLoginState() {
        sharedPreferences.edit().clear().apply()
    }
}
