package com.example.biblioteca.logins

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.biblioteca.`1HomeActivity`
import com.example.biblioteca.R
import com.example.biblioteca.auth.AuthService
import com.example.biblioteca.utils.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * es la actividad que maneja el proceso de inicio de sesión de los usuarios.
 * Permite al usuario introducir su correo electrónico y contraseña, y gestionar el estado de "recordarme".
 */
class LoginActivity : AppCompatActivity() {


    private lateinit var emailEditText: EditText // Campo para introducir el correo electrónico.
    private lateinit var passwordEditText: EditText // Campo para introducir la contraseña.
    private lateinit var btnTogglePassword: ImageButton // Botón para mostrar/ocultar la contraseña.
    private lateinit var loginButton: Button // Botón para iniciar sesión.
    private lateinit var btnGoToRegister: TextView // Botón de texto para ir a la pantalla de registro.
    private lateinit var checkRemember: CheckBox // Checkbox para "recordar" al usuario.
    private lateinit var authService: AuthService // Instancia del servicio de autenticación.
    private lateinit var prefs: PreferencesManager // Instancia del gestor de preferencias.
    private var passwordVisible = false // Bandera para controlar la visibilidad de la contraseña.

    /**
     * Se llama cuando la configuración del dispositivo cambia (ej. rotación de pantalla).
     * En este caso, no se implementa ninguna lógica específica, solo se llama al método de la superclase.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    /**
     * Se llama cuando la actividad es creada por primera vez.
     * Aquí se inicializan las vistas, se configuran los listeners y se comprueba el estado de sesión.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById<TextView>(R.id.btnGoToRegister)
        checkRemember = findViewById(R.id.checkRemember)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)

        // Inicializa el servicio de autenticación y el gestor de preferencias.
        authService = AuthService(this)
        prefs = PreferencesManager(this)

        // Establece el icono inicial para el botón de mostrar/ocultar contraseña (oculta por defecto).
        btnTogglePassword.setImageResource(R.drawable.ic_visibility)

        // Configura el listener de toque para el botón de mostrar/ocultar contraseña.
        // La contraseña se muestra mientras el usuario mantiene presionado el botón.
        btnTogglePassword.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { // Cuando el dedo toca la pantalla.
                    passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // Hace la contraseña visible.
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility) // Mantiene el icono de "visible".
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { // Cuando el dedo se levanta o el toque es cancelado.
                    // Vuelve a ocultar la contraseña.
                    passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility) // Vuelve al icono de "oculto".
                }
            }
            true // Indica que el evento ha sido manejado.
        }

        // Comprueba si el usuario ya ha iniciado sesión.
        // Si es así, navega directamente a HomeActivity y finaliza esta actividad.
        if (prefs.isLoggedIn()) {
            val intent = Intent(this, `1HomeActivity`::class.java)

            // Estas banderas aseguran que HomeActivity sea la única actividad en la pila
            // y que no se pueda volver a LoginActivity con el botón de retroceso.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Cierra LoginActivity.
            return // Sale del método onCreate para no ejecutar más código.
        }

        // Configura el listener para el botón de iniciar sesión.
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString() // Obtiene el texto del campo de email.
            val password = passwordEditText.text.toString() // Obtiene el texto del campo de contraseña.
            val remember = checkRemember.isChecked // Obtiene el estado del checkbox "recordarme".

            // ✅ Valida que los campos de email y contraseña no estén vacíos.
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this@LoginActivity, "Aún hay campos en blanco por rellenar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Sale del listener si hay campos vacíos.
            }

            // Lanza una corrutina en el hilo principal (Main) para realizar el login.
            CoroutineScope(Dispatchers.Main).launch {
                val result = authService.login(email, password, remember) // Llama al servicio de autenticación.
                if (result) {
                    // Si el login es exitoso, guarda el email en SharedPreferences (podría ser para pre-llenado).
                    val sharedPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
                    sharedPrefs.edit().putString("email", email).apply()

                    // Navega a HomeActivity, limpiando la pila de actividades.
                    val intent = Intent(this@LoginActivity, `1HomeActivity`::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Cierra LoginActivity.
                } else {
                    // Si el login falla, muestra un mensaje de error al usuario.
                    Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configura el listener para el botón de ir a la pantalla de registro.
        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java)) // Inicia RegisterActivity.
        }
    }
}
