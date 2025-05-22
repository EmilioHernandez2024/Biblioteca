package com.example.biblioteca.model

/**
 * Clase de datos que representa una categoría de libros.
 * Contiene el nombre de la categoría y una lista de [Libro]s asociados.
 */

data class CategoriaLibro(
    val nombre: String, // Nombre de la categoría (ej. "Ciencia Ficción").
    val libros: List<Libro>  // Lista de libros dentro de esta categoría.
)
