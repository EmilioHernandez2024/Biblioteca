package com.example.biblioteca.utils

import com.example.biblioteca.R
import com.example.biblioteca.model.CategoriaLibro
import com.example.biblioteca.model.Libro

object LibroData {

    val librosRecientes = listOf(
        Libro("Matemática III", R.drawable.ic_launcher_foreground),
        Libro("Física I", R.drawable.ic_launcher_foreground),
        Libro("Ingeniería de Software", R.drawable.ic_launcher_foreground)
    )

    val categorias = listOf(
        CategoriaLibro("Ingeniería", listOf(
            Libro("Matemática III", R.drawable.ic_launcher_foreground),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground)
        )),
        CategoriaLibro("Programación", listOf(
            Libro("Introducción a Java", R.drawable.ic_launcher_foreground),
            Libro("Android Studio Básico", R.drawable.ic_launcher_foreground)
        )),
        CategoriaLibro("Física", listOf(
            Libro("Física I", R.drawable.ic_launcher_foreground),
            Libro("Termodinámica", R.drawable.ic_launcher_foreground)
        ))
    )

    // Extra: libros combinados de todas las categorías
    val todosLosLibros: List<Libro> = categorias.flatMap { it.libros }.distinctBy { it.titulo }
}
