package com.example.biblioteca

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.biblioteca.fragment.*
import com.example.biblioteca.logins.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var switch: SwitchMaterial
    private lateinit var switchItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // Verifica si el usuario está logueado, si no redirige al login
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            // Si no está logueado (porque no marcó "recordar"), no entres
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Si no quiere recordar sesión, la borramos al cerrar HomeActivity
            val remember = sharedPreferences.getBoolean("remember_me", false)
            if (!remember) {
                sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
            }
        }

        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)
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

        val menu = navigationView.menu
        switchItem = menu.findItem(R.id.nav_switch)
        switch = switchItem.actionView!!.findViewById(R.id.nav_switch)

        switch.isChecked = isDarkMode
        switchItem.title = if (isDarkMode) getString(R.string.modo_dia) else getString(R.string.modo_noche)

        switch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("is_dark_mode", isChecked).apply()
            switchItem.title = if (isChecked) getString(R.string.modo_dia) else getString(R.string.modo_noche)

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
        }

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

    override fun onDestroy() {
        super.onDestroy()

        val rememberMe = sharedPreferences.getBoolean("remember_me", false)
        if (!rememberMe) {
            sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
        }
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
            R.id.nav_settings -> replaceFragment(FragmentSetting())
            R.id.nav_favoritos -> replaceFragment(FragmentFavorito())
            R.id.nav_buscar -> replaceFragment(FragmentBuscar())
            R.id.nav_logout -> {
                sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
