package com.example.biblioteca.logins

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.biblioteca.HomeActivity
import com.example.biblioteca.R
import com.example.biblioteca.auth.AuthService
import com.example.biblioteca.utils.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var loginButton: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var checkRemember: CheckBox
    private lateinit var authService: AuthService
    private lateinit var prefs: PreferencesManager
    private var passwordVisible = false

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister)
        checkRemember = findViewById(R.id.checkRemember)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)

        authService = AuthService(this)
        prefs = PreferencesManager(this)

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

        // ✅ Si ya está logueado, ir directo a Home limpiando el stack
        if (prefs.isLoggedIn()) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val remember = checkRemember.isChecked

            CoroutineScope(Dispatchers.Main).launch {
                val result = authService.login(email, password, remember)
                if (result) {
                    val sharedPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
                    sharedPrefs.edit().putString("email", email).apply()

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
