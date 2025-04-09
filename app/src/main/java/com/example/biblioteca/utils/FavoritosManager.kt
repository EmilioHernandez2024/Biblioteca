package com.example.biblioteca.utils

import android.content.Context
import com.example.biblioteca.model.Libro
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritosManager {
    private const val PREFS_NAME = "favoritos_prefs"
    private const val KEY_PREFIX = "favoritos_usuario_"

    private val gson = Gson()

    fun agregarFavorito(context: Context, usuario: String, libro: Libro) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lista = obtenerFavoritos(context, usuario).toMutableList()
        if (!lista.any { it.titulo == libro.titulo }) {
            lista.add(libro)
            prefs.edit().putString("$KEY_PREFIX$usuario", gson.toJson(lista)).apply()
        }
    }

    fun obtenerFavoritos(context: Context, usuario: String): List<Libro> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("$KEY_PREFIX$usuario", null)
        return if (json != null) {
            val type = object : TypeToken<List<Libro>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    fun esFavorito(context: Context, usuario: String, libro: Libro): Boolean {
        val favoritos = obtenerFavoritos(context, usuario)
        return favoritos.any { it.titulo == libro.titulo }
    }

    fun eliminarFavorito(context: Context, usuario: String, libro: Libro) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val nuevaLista = obtenerFavoritos(context, usuario).filterNot { it.titulo == libro.titulo }
        prefs.edit().putString("$KEY_PREFIX$usuario", gson.toJson(nuevaLista)).apply()
    }

}
