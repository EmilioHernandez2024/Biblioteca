package com.example.biblioteca

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializa las vistas
        usernameEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)

        // Inicializa SharedPreferences para almacenar el estado de inicio de sesión
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // Maneja el clic en el botón de inicio de sesión
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Verifica si el usuario y la contraseña coinciden con los datos predeterminados
            if (username == "usuario" && password == "123456") {
                // Guarda el estado de inicio de sesión en SharedPreferences
                sharedPreferences.edit().putBoolean("is_logged_in", true).apply()

                // Inicia la actividad principal
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Muestra un mensaje de error si las credenciales son incorrectas
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        // Verifica si el usuario ha iniciado sesión previamente
        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            // El usuario ya ha iniciado sesión, inicia la actividad principal
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}