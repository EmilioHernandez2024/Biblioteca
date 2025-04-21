package com.example.biblioteca.logins

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.biblioteca.R
import com.example.biblioteca.auth.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnRegister: Button
    private lateinit var btnGoToLogin: Button
    private lateinit var authService: AuthService
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailEditText = findViewById(R.id.etEmailRegister)
        passwordEditText = findViewById(R.id.etPasswordRegister)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoToLogin = findViewById(R.id.btnGoToLogin)

        authService = AuthService(this)

        // Establecer el icono inicial para contraseña oculta
        btnTogglePassword.setImageResource(R.drawable.ic_visibility)

        // Mostrar/Ocultar contraseña al mantener presionado
        btnTogglePassword.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Cuando se presiona el botón, mostrar la contraseña
                    passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility) // Cambiar icono a visible
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Cuando se suelta el botón (o se cancela el toque), ocultar la contraseña
                    passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility) // Cambiar icono a oculto
                }
            }
            // Indica que hemos manejado el evento táctil
            true
        }

        btnRegister.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar el correo antes de continuar con el registro
            if (!isEmailValido(email)) {
                Toast.makeText(this@RegisterActivity, "Por favor, ingrese un correo válido (gmail, hotmail, live,outlook.)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val result = authService.register(email, password)
                if (result) {
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error al registrar", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // Función para validar el correo con regex
    private fun isEmailValido(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(gmail|hotmail|outlook|live)\\.com$")
        return emailRegex.matches(email)
    }
}
