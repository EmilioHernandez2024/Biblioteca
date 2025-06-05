package com.example.biblioteca.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * es la actividad que maneja el proceso de preferencia de la cuenta
 * se puede ver los libros favoritos que se han guardado y asu vez eliminarlo el proceso
 */

object FavoritosManager {
    private const val PREFS_NAME = "favoritos_prefs"
    private const val KEY_PREFIX = "favoritos_usuario_"
    /**
     * se puede ver que se guarda en gson los datos preferidos
     */
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
