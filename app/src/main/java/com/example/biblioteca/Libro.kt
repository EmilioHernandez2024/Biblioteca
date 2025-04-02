package com.example.biblioteca.model

import com.example.biblioteca.R  // Importa R para acceder a los recursos

data class Libro(
    val titulo: String,
    val imagen: Int = R.drawable.ic_launcher_foreground // Imagen por defecto
)
