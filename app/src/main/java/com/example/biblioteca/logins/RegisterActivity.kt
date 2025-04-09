package com.example.biblioteca.logins

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.biblioteca.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoToLogin = findViewById<Button>(R.id.btnGoToLogin)

        btnRegister.setOnClickListener {
            // Llevar al usuario a la pantalla de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Evita que vuelva a la pantalla de registro con "Atrás"
        }

        btnGoToLogin.setOnClickListener {
            // Regresar al login sin registrar nada
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
