package com.example.biblioteca.utils

import com.example.biblioteca.R
import com.example.biblioteca.model.CategoriaLibro
import com.example.biblioteca.model.Libro

object LibroData {

    val librosRecientes = listOf(
        Libro(
            titulo = "Matemática III",
            pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
            imagen = R.drawable.ic_launcher_foreground
        ),
        Libro(
            titulo = "Fisica IV",
            pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
            imagen = R.drawable.ic_launcher_foreground
        ),
        Libro(
            titulo = " Cálculo II",
            pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
            imagen = R.drawable.ic_launcher_foreground
        ),
    )

    val categorias = listOf(
        CategoriaLibro("Ingeniería", listOf(
            Libro(
                titulo = " Cálculo II",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
                imagen = R.drawable.ic_launcher_foreground
            ),
            Libro(
                titulo = "Matemática III",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
                imagen = R.drawable.ic_launcher_foreground
            ),
        )),
        CategoriaLibro("Programación", listOf(
            Libro(
                titulo = "Introducion a Java",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
                imagen = R.drawable.ic_launcher_foreground
            ),
            Libro(
                titulo = "Android studio Basico ",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
                imagen = R.drawable.ic_launcher_foreground
            )
        )),
        CategoriaLibro("Física", listOf(
            Libro(
                titulo = "Fisica I",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
                imagen = R.drawable.ic_launcher_foreground
            ),
            Libro(
                titulo = "Fisica IV",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
                imagen = R.drawable.ic_launcher_foreground
            ),
            Libro(
                titulo = "Termodinamica",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/68017e3e003b830b144b/view?project=67f9710b0009513a166d&mode=admin",
                imagen = R.drawable.ic_launcher_foreground
            ),
        ))
    )

    // Extra: libros combinados de todas las categorías
    val todosLosLibros: List<Libro> = categorias.flatMap { it.libros }.distinctBy { it.titulo }
}
