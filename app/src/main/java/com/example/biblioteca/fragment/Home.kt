package com.example.biblioteca.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.adapter.LibroAdapter
import com.example.biblioteca.model.Libro

class Home : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Busca el RecyclerView dentro del layout del fragmento
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerLibrosRecientes)

        // Asegúrate de que recyclerView no sea null antes de usarlo
        if (recyclerView != null) {
            // Lista de libros de ejemplo
            val librosRecientes = listOf(
                Libro("Matemática II", R.drawable.ic_launcher_foreground )
            )

            // Configuración del RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = LibroAdapter(librosRecientes)
        } else {
            // Mensaje de error en Logcat si el RecyclerView no existe en el layout
            android.util.Log.e("HomeFragment", "Error: recyclerLibrosRecientes es null")
        }
    }
}
