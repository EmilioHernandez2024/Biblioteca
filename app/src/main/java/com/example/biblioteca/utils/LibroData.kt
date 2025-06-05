package com.example.biblioteca.utils

import com.example.biblioteca.R
import com.example.biblioteca.model.CategoriaLibro
import com.example.biblioteca.model.Libro

/**
 * aqui es donde se agregan los libros con su url,ña url de la imagen y añadir su categoria para que el
 * modo busqueda pueda encontrarlo
 */

object LibroData {

    val librosRecientes = listOf(
        Libro(
            titulo = "Matemática III",
            pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680dee1c001ad393f9d7/view?project=67f9710b0009513a166d&mode=admin",
            imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f1563002e7224c9ce/view?project=67f9710b0009513a166d&mode=admin",
            categoria = "Ingeniería"
        ),
        Libro(
            titulo = "Fisica IV",
            pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f1760003cef40e076/view?project=67f9710b0009513a166d&mode=admin",
            imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f176900065e5755d6/view?project=67f9710b0009513a166d&mode=admin",
            categoria = "Fisica"
        ),
        Libro(
            titulo = " Cálculo II",
            pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680df07c000efa5586dd/view?project=67f9710b0009513a166d&mode=admin",
            imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f155b0005e2dc7c9d/view?project=67f9710b0009513a166d&mode=admin",
            categoria = "Ingeniería"
        ),
    )

    val categorias = listOf(
        CategoriaLibro("Ingeniería", listOf(
            Libro(
                titulo = " Cálculo II",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680df07c000efa5586dd/view?project=67f9710b0009513a166d&mode=admin",
                imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f155b0005e2dc7c9d/view?project=67f9710b0009513a166d&mode=admin",
                categoria = "Ingeniería"
            ),
            Libro(
                titulo = "Matemática III",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680dee1c001ad393f9d7/view?project=67f9710b0009513a166d&mode=admin",
                imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f1563002e7224c9ce/view?project=67f9710b0009513a166d&mode=admin",
                categoria = "Ingeniería"
            ),
        )),
        CategoriaLibro("Programación", listOf(
            Libro(
                titulo = "Introducion a Programación",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680df19c001de25d326c/view?project=67f9710b0009513a166d&mode=admin",
                imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f14de00364e184328/view?project=67f9710b0009513a166d&mode=admin",
                categoria = "Ingeniería"
            ),

        )),
        CategoriaLibro("Física", listOf(
            Libro(
                titulo = "Fisica General",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f16e30020dcfacab9/view?project=67f9710b0009513a166d&mode=admin",
                imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f16c5000d120647d8/view?project=67f9710b0009513a166d&mode=admin",
                categoria = "Física"
            ),
            Libro(
                titulo = "Fisica IV",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f1760003cef40e076/view?project=67f9710b0009513a166d&mode=admin",
                imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f176900065e5755d6/view?project=67f9710b0009513a166d&mode=admin",
                categoria = "Física"
            ),
            Libro(
                titulo = "Termodinamica",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f1678001685edac45/view?project=67f9710b0009513a166d&mode=admin",
                imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f16d3000d62d3ab7c/view?project=67f9710b0009513a166d&mode=admin",
                categoria = "Física"
            ),
        )),

                CategoriaLibro("Estudios sociales ", listOf(
            Libro(
                titulo = "Estudios sociales y civica II",
                pdfUrl = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680defde000471167f8b/view?project=67f9710b0009513a166d&mode=admin",
                imagen = "https://fra.cloud.appwrite.io/v1/storage/buckets/67f98648002cfc67b610/files/680f156b000d7c418162/view?project=67f9710b0009513a166d&mode=admin",
                categoria = "Estudios sociales"
            ),

        ))
    )

    // Extra: libros combinados de todas las categorías
    val todosLosLibros: List<Libro> = categorias.flatMap { it.libros }.distinctBy { it.titulo }
}
