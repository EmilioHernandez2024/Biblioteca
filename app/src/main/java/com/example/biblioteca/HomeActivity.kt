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

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var switch: SwitchMaterial
    private lateinit var switchItem: MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // Verifica si el usuario está logueado, si no redirige al login
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val remember = sharedPreferences.getBoolean("remember_me", false)
        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)

        // Aplicar tema
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val headerView = navigationView.getHeaderView(0)
        val tvAccountName = headerView.findViewById<TextView>(R.id.tvAccountName) // ← ID de tu TextView

// Obtener el correo desde SharedPreferences
        val email = sharedPreferences.getString("email", "Correo no disponible")
        tvAccountName.text = email
        ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        ).apply {
            syncState()
            drawerLayout.addDrawerListener(this)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Home())
                .commit()
        }

        // Configuración del switch para modo oscuro
        val menu = navigationView.menu
        switchItem = menu.findItem(R.id.nav_switch)
        switch = switchItem.actionView!!.findViewById(R.id.nav_switch)
        switch.isChecked = isDarkMode
        switchItem.title = if (isDarkMode) getString(R.string.modo_dia) else getString(R.string.modo_noche)

        switch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit()
                .putBoolean("is_dark_mode", isChecked)
                .putBoolean("changing_theme", true)
                .apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            recreate()
        }


        // Manejo del botón de retroceso personalizado
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fm = supportFragmentManager
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else if (fm.backStackEntryCount > 0) {
                    fm.popBackStack()
                } else {
                    if (fm.findFragmentById(R.id.fragment_container) !is Home) {
                        replaceFragment(Home(), false)
                    } else {
                        finish()
                    }
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()

        val rememberMe = sharedPreferences.getBoolean("remember_me", false)
        val isChangingThemeNow = sharedPreferences.getBoolean("changing_theme", false)

        if (!rememberMe && !isChangingThemeNow) {
            sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
        }
    }
    override fun onResume() {
        super.onResume()
        // Restablece el flag de cambio de tema
        sharedPreferences.edit().putBoolean("changing_theme", false).apply()
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate(null, 1)
        }
        transaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(Home(), false)
            R.id.nav_favoritos -> replaceFragment(FragmentFavorito())
            R.id.nav_buscar -> replaceFragment(FragmentBuscar())
            R.id.nav_logout -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val authService = AuthService(this@HomeActivity)
                    val success = authService.logout()

                    withContext(Dispatchers.Main) {
                        // También limpiamos preferencias manualmente por si falló el logout
                        val prefs = PreferencesManager(this@HomeActivity)
                        prefs.saveLoginState(false)
                        prefs.saveRememberMe(false)

                        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                true
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
