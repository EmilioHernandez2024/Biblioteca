package com.example.biblioteca

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.biblioteca.auth.AuthService
import com.example.biblioteca.fragment.FragmentBuscar
import com.example.biblioteca.fragment.FragmentFavorito

import com.example.biblioteca.fragment.Home
import com.example.biblioteca.logins.LoginActivity
import com.example.biblioteca.utils.PreferencesManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *  es la actividad principal de la aplicación después del inicio de sesión.
 * Contiene un [DrawerLayout] para la navegación, gestiona los fragmentos y el modo oscuro.
 */
class `1HomeActivity` : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout // El diseño del cajón de navegación.
    private lateinit var sharedPreferences: SharedPreferences // Para acceder a las preferencias de la aplicación.
    private lateinit var switch: SwitchMaterial // El switch dentro del menú de navegación para el tema.
    private lateinit var switchItem: MenuItem // El ítem del menú que contiene el switch.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Si esta actividad no es la raíz de la tarea (es decir, se inició desde otra actividad
        // y no hay actividades previas en la pila), se cierra para evitar duplicados al lanzar.
        if (!isTaskRoot) {
            finish()
            return
        }

        // Inicializa SharedPreferences para acceder a las preferencias de login.
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // **Verificación de Sesión:**
        // Comprueba si el usuario está logueado. Si no, redirige a LoginActivity y finaliza esta actividad.
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Cierra HomeActivity si el usuario no está logueado.
            return
        }

        // Recupera la preferencia de "recordarme" y el estado del modo oscuro.
        val remember = sharedPreferences.getBoolean("remember_me", false)
        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)

        // **Aplicar Tema (Modo Oscuro/Claro):**
        // Establece el modo nocturno predeterminado para toda la aplicación basado en la preferencia guardada.
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES // Si isDarkMode es true, activa el modo noche.
            else AppCompatDelegate.MODE_NIGHT_NO // Si no, desactiva el modo noche (activa el modo día).
        )

        setContentView(R.layout.activity_main) // Establece el layout principal de la actividad.

        // Configuración de la Toolbar como ActionBar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializa el DrawerLayout y el NavigationView (menú lateral).
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this) // Establece el listener para los clics en los ítems del menú.

        // **Configuración del Header del NavigationView:**
        // Obtiene la vista del header del menú de navegación para acceder a sus elementos.
        val headerView = navigationView.getHeaderView(0)
        val tvAccountName = headerView.findViewById<TextView>(R.id.tvAccountName) // ID del TextView donde se mostrará el correo.

        // Obtiene el correo del usuario desde SharedPreferences y lo establece en el TextView del header.
        val email = sharedPreferences.getString("email", "Correo no disponible")
        tvAccountName.text = email

        // Configura el ActionBarDrawerToggle para integrar la Toolbar con el DrawerLayout.
        // Esto crea el icono de "hamburguesa" que abre/cierra el cajón.
        ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav // Cadenas para accesibilidad.
        ).apply {
            syncState() // Sincroniza el estado del icono de la hamburguesa.
            drawerLayout.addDrawerListener(this) // Añade el listener para manejar la apertura/cierre del cajón.
        }

        // **Carga Inicial del Fragmento:**
        // Carga el fragmento Home si la actividad se inicia por primera vez (savedInstanceState es null).
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Home()) // Reemplaza el contenido del contenedor por el fragmento Home.
                .commit() // Confirma la transacción.
        }

        // **Configuración del Switch de Modo Oscuro en el Menú:**
        // Encuentra el ítem del menú que contiene el switch.
        val menu = navigationView.menu
        switchItem = menu.findItem(R.id.nav_switch)
        // Accede al SwitchMaterial dentro de la vista de acción del MenuItem.
        switch = switchItem.actionView!!.findViewById(R.id.nav_switch)
        switch.isChecked = isDarkMode // Establece el estado inicial del switch basado en la preferencia guardada.
        // Actualiza el texto del ítem del menú para reflejar el estado actual del modo.
        switchItem.title = if (isDarkMode) getString(R.string.modo_dia) else getString(R.string.modo_noche)

        // Configura el listener para el cambio de estado del switch del modo oscuro.
        switch.setOnCheckedChangeListener { _, isChecked ->
            // Guarda la nueva preferencia del modo oscuro y un flag temporal para indicar que el tema está cambiando.
            sharedPreferences.edit()
                .putBoolean("is_dark_mode", isChecked)
                .putBoolean("changing_theme", true) // Flag para evitar cerrar sesión accidentalmente.
                .apply()

            // Aplica el nuevo modo oscuro/claro inmediatamente.
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            recreate() // Recrea la actividad para aplicar el nuevo tema instantáneamente.
        }

        // **Manejo del Botón de Retroceso Personalizado:**
        // Sobrescribe el comportamiento predeterminado del botón de retroceso del sistema.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fm = supportFragmentManager
                // Si el cajón de navegación está abierto, ciérralo.
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                // Si hay fragmentos en la pila de retroceso, saca el último.
                else if (fm.backStackEntryCount > 0) {
                    fm.popBackStack()
                }
                // Si no hay fragmentos en la pila y el fragmento actual no es Home, navega a Home.
                else {
                    if (fm.findFragmentById(R.id.fragment_container) !is Home) {
                        replaceFragment(Home(), false) // Reemplaza por Home sin añadir a la pila.
                    }
                    // Si ya estamos en Home y no hay fragmentos en la pila, finaliza la actividad.
                    else {
                        finish()
                    }
                }
            }
        })
    }

    /**
     * Se llama cuando la actividad ya no es visible para el usuario.
     * Aquí se gestiona el estado de la sesión si el usuario no ha marcado "recordarme".
     */
    override fun onStop() {
        super.onStop()

        val rememberMe = sharedPreferences.getBoolean("remember_me", false)
        val isChangingThemeNow = sharedPreferences.getBoolean("changing_theme", false)

        // Si el usuario no marcó "recordarme" y no se está cambiando el tema,
        // establece el estado de "is_logged_in" a falso para que la próxima vez tenga que loguearse.
        if (!rememberMe && !isChangingThemeNow) {
            sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
        }
    }

    /**
     * Se llama cuando la actividad está a punto de volver a ser visible.
     * Restablece el flag `changing_theme` para futuras operaciones.
     */
    override fun onResume() {
        super.onResume()
        // Restablece el flag de cambio de tema a falso.
        sharedPreferences.edit().putBoolean("changing_theme", false).apply()
    }

    /**
     * Reemplaza el fragmento actual en el contenedor principal.
     * @param fragment El nuevo fragmento a mostrar.
     * @param addToBackStack `true` para añadir la transacción a la pila de retroceso (permite volver), `false` para no añadir.
     */
    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Reemplaza el fragmento en el contenedor.
        if (addToBackStack) {
            transaction.addToBackStack(null) // Añade la transacción a la pila de retroceso.
        }
        // Si hay elementos en la pila de retroceso, los saca para limpiar la pila antes de añadir el nuevo fragmento.
        // Esto es útil para evitar que se acumulen muchos fragmentos en la pila innecesariamente.
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate(null, 1) // Limpia la pila hasta el inicio.
        }
        transaction.commit() // Confirma la transacción del fragmento.
    }

    /**
     * Se llama cuando un ítem del menú de navegación es seleccionado.
     * @param item El ítem del menú seleccionado.
     * @return `true` si el evento fue manejado, `false` en caso contrario.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(Home(), false) // Navega a Home (sin añadir a la pila).
            R.id.nav_favoritos -> replaceFragment(FragmentFavorito()) // Navega a Favoritos.
            R.id.nav_buscar -> replaceFragment(FragmentBuscar()) // Navega a Buscar.
            R.id.nav_logout -> { // Cuando se selecciona la opción de cerrar sesión.
                // Lanza una corrutina en el contexto de IO para realizar la operación de logout.
                CoroutineScope(Dispatchers.IO).launch {
                    val authService = AuthService(this@`1HomeActivity`) // Crea una instancia del servicio de autenticación.
                    val success = authService.logout() // Intenta cerrar la sesión.

                    // Una vez que la operación de logout ha terminado, cambia al hilo principal (Main)
                    // para actualizar la UI y navegar.
                    withContext(Dispatchers.Main) {
                        // Limpiamos las preferencias de login manualmente, incluso si el logout de AuthService
                        // fallara, para asegurar que el usuario sea redirigido a LoginActivity.
                        val prefs = PreferencesManager(this@`1HomeActivity`)
                        prefs.saveLoginState(false) // Establece el estado de login a falso.
                        prefs.saveRememberMe(false) // Desactiva "recordarme".

                        // Navega a LoginActivity y limpia la pila de actividades.
                        val intent = Intent(this@`1HomeActivity`, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // Cierra HomeActivity.
                    }
                }
                true // Indica que el evento de clic fue manejado.
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START) // Cierra el cajón de navegación después de seleccionar un ítem.
        return true
    }
}
