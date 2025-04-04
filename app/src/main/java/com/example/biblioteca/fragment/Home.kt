package com.example.biblioteca.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.adapter.CategoriaAdapter
import com.example.biblioteca.adapter.LibroAdapter
import com.example.biblioteca.model.CategoriaLibro
import com.example.biblioteca.model.Libro

class Home : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerLibrosRecientes = view.findViewById<RecyclerView>(R.id.recyclerLibrosRecientes)
        val recyclerCategorias = view.findViewById<RecyclerView>(R.id.recyclerCategorias)

        // Lista de libros recientes (Sin categoría)
        val librosRecientes = listOf(
            Libro("Matemática III", R.drawable.ic_launcher_foreground),
            Libro("Física I", R.drawable.ic_launcher_foreground),
            Libro("Ingeniería de Software", R.drawable.ic_launcher_foreground)
        )

        // Lista de categorías con libros
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

        // Adaptador para libros recientes (horizontal)
        recyclerLibrosRecientes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerLibrosRecientes.adapter = LibroAdapter(librosRecientes)

        // Adaptador para categorías (vertical)
        recyclerCategorias.layoutManager = LinearLayoutManager(requireContext())
        recyclerCategorias.adapter = CategoriaAdapter(categorias)
    }
}

