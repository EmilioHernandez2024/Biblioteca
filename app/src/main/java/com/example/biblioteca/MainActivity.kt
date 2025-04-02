package com.example.biblioteca

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.biblioteca.fragment.FragmentSetting
import com.example.biblioteca.fragment.Home
import com.example.biblioteca.fragment.FragmentFavorito
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout // Drawer para el menú lateral
    private var currentFragment: Fragment = Home() // Fragmento actual (se inicia en Home)
    private lateinit var sharedPreferences: SharedPreferences // Para guardar las configuraciones del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar la Toolbar (barra superior)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configurar el Navigation Drawer (menú lateral)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Agregar el botón de hamburguesa para abrir/cerrar el menú lateral
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Si la app se abre por primera vez, cargar el fragmento Home
        if (savedInstanceState == null) {
            replaceFragment(Home()) // Mostrar Home al inicio
            navigationView.setCheckedItem(R.id.nav_home) // Marcar Home como seleccionado
        }

        // Cargar la configuración de modo noche desde SharedPreferences
        sharedPreferences = getSharedPreferences("config_prefs", Context.MODE_PRIVATE)
        val modoNocheActivado = sharedPreferences.getBoolean("modo_noche", false) // Leer si el usuario activó el modo noche
        aplicarModoNoche(modoNocheActivado) // Aplicar el modo guardado

        // Configurar el Switch del modo noche en el NavigationView
        val switchItem = navigationView.menu.findItem(R.id.nav_switch) // Obtener el switch del menú
        val switchTema = switchItem.actionView as? SwitchMaterial // Convertirlo a un SwitchMaterial

        switchTema?.isChecked = modoNocheActivado // Marcar el switch según la configuración guardada
        actualizarTextoSwitch(switchItem, modoNocheActivado) // Cambiar el texto a "Modo Día" o "Modo Noche"

        // Evento cuando el usuario cambia el estado del switch
        switchTema?.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("modo_noche", isChecked).apply() // Guardar el nuevo estado en SharedPreferences
            actualizarTextoSwitch(switchItem, isChecked) // Cambiar el texto del switch
            aplicarModoNoche(isChecked) // Aplicar el nuevo modo
        }
    }


    // Aplica el Modo Noche o Modo Día dependiendo del valor booleano recibido.
    // Se usa recreate() para evitar el parpadeo negro al cambiar de tema.

    private fun aplicarModoNoche(activar: Boolean) {
        val modo = if (activar) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != modo) {
            AppCompatDelegate.setDefaultNightMode(modo)
            recreate() // Esto reinicia la actividad para aplicar los cambios correctamente
        }
    }


    //Actualiza el texto del switch en el menú lateral

    private fun actualizarTextoSwitch(item: MenuItem, esModoNoche: Boolean) {
        item.title = if (esModoNoche) getString(R.string.modo_dia) else getString(R.string.modo_noche)
    }


    //Reemplaza el fragmento actual con el que se pase como parámetro

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        currentFragment = fragment // Guardar el fragmento actual
    }


    //Maneja los clics en los elementos del menú lateral

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(Home()) // Cambia a Home
            R.id.nav_settings -> replaceFragment(FragmentSetting()) // Cambia a Configuración
            R.id.nav_favoritos -> replaceFragment(FragmentFavorito()) // Cambia a Favoritos
            R.id.nav_logout -> {
                sharedPreferences.edit().putBoolean("is_logged_in", false).apply() // Cierra la sesión del usuario
                finish() // Cierra la app
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START) // Cierra el menú después de seleccionar una opción
        return true
    }


    //Maneja el botón de retroceso (Back)

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START) // Si el menú está abierto, ciérralo
        } else {
            if (currentFragment !is Home) {
                replaceFragment(Home()) // Si no estamos en Home, volver a Home
            } else {
                finishAffinity() // Si ya estamos en Home, salir de la app
            }
        }
    }
}
