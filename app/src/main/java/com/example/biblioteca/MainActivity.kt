package com.example.biblioteca

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.biblioteca.fragment.FragmentFavorito
import com.example.biblioteca.fragment.FragmentSetting
import com.example.biblioteca.fragment.Home
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private var currentFragment: Fragment = Home()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var switch: SwitchMaterial
    private lateinit var switchItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar preferencias del usuario
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // Obtener el modo guardado en SharedPreferences
        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)

        // Detectar el modo actual del sistema y aplicar el tema
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES || isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.activity_main)

        // Configuración de la barra de herramientas
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configuración del DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Configurar el botón de menú (Drawer Toggle)
        ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        ).apply {
            syncState()
            drawerLayout.addDrawerListener(this)
        }

        // Si no hay estado guardado, iniciar con el fragmento Home
        if (savedInstanceState == null) {
            replaceFragment(Home())
            navigationView.setCheckedItem(R.id.nav_home)
        }

        // Inicializar Switch desde el menú de navegación
        val menu = navigationView.menu
        switchItem = menu.findItem(R.id.nav_switch)
        val actionView = switchItem.actionView
        if (actionView != null) {
            switch = actionView.findViewById(R.id.nav_switch)
        }

        // Configurar el estado inicial del Switch
        switch.isChecked = isDarkMode
        switchItem.title = if (isDarkMode) getString(R.string.modo_dia) else getString(R.string.modo_noche)

        // Listener del Switch para cambiar el tema
        switch.setOnCheckedChangeListener { _, isChecked ->
            // Guardar preferencia del usuario
            val editor = sharedPreferences.edit()
            editor.putBoolean("is_dark_mode", isChecked)
            editor.apply()

            // Cambiar el texto del menú
            switchItem.title = if (isChecked) getString(R.string.modo_dia) else getString(R.string.modo_noche)

            // Aplicar el tema
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Verificar si el usuario ha iniciado sesión
        if (!sharedPreferences.getBoolean("is_logged_in", false)) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Guarda el estado anterior en la pila de retroceso
            .commit()
        currentFragment = fragment
    }


    // Manejar los clics del menú de navegación
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(Home())
            R.id.nav_settings -> replaceFragment(FragmentSetting())
            R.id.nav_favoritos -> replaceFragment(FragmentFavorito())
            R.id.nav_logout -> {
                // Cerrar sesión
                sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Manejar el botón de retroceso
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (currentFragment !is Home) {
                replaceFragment(Home())
            } else {
                finishAffinity()
            }
        }
    }
}
