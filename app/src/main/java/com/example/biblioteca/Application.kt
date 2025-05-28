package com.example.biblioteca

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.biblioteca.appwrite.AppwriteClient

/**
 * [BibliotecaApp] es la clase de aplicación personalizada.
 * Se ejecuta cuando la aplicación se inicia y es para configuraciones globales.
 */
class BibliotecaApp : Application() {
    /**
     * Se llama cuando la aplicación se inicia por primera vez.
     * Aquí se recupera la preferencia del modo oscuro del usuario y se aplica globalmente.
     */
    override fun onCreate() {
        super.onCreate() // Llama al onCreate de la clase base Application.

        // Obtiene las SharedPreferences con el nombre "login_prefs".
        // Estas preferencias se utilizan para guardar información como el estado de login y el modo oscuro.

        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        // Recupera el valor booleano de la clave "is_dark_mode". Si no existe, el valor por defecto es `false`.

        val isDarkMode = prefs.getBoolean("is_dark_mode", false)

        // Establece el modo nocturno predeterminado para toda la aplicación.
        // Si `isDarkMode` es true, se activa el modo nocturno; de lo contrario, se desactiva.

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES // Activa el modo oscuro.
            else AppCompatDelegate.MODE_NIGHT_NO // Desactiva el modo oscuro.
        )
    }
}