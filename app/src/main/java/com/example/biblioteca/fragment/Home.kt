package com.example.biblioteca.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.MainActivity
import com.example.biblioteca.R
import com.example.biblioteca.adapter.CategoriaAdapter
import com.example.biblioteca.adapter.LibroAdapter
import com.example.biblioteca.utils.LibroData

class Home : Fragment() {

    private lateinit var recyclerViewRecientes: RecyclerView
    private lateinit var recyclerViewCategorias: RecyclerView
    private lateinit var libroAdapter: LibroAdapter
    private lateinit var categoriaAdapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Asegúrate que los IDs coincidan con tu XML
        recyclerViewRecientes = view.findViewById(R.id.recyclerLibrosRecientes)
        recyclerViewCategorias = view.findViewById(R.id.recyclerCategorias)

        // Adapter para libros recientes
        libroAdapter = LibroAdapter(LibroData.librosRecientes) { libro ->
            val fragment = FragmentDetalleLibro.newInstance(libro.titulo)
            (activity as? MainActivity)?.replaceFragment(FragmentDetalleLibro.newInstance(libro.titulo))

        }

        recyclerViewRecientes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewRecientes.adapter = libroAdapter

        // Adapter para categorías
        categoriaAdapter = CategoriaAdapter(LibroData.categorias) { libro ->
            val fragment = FragmentDetalleLibro.newInstance(libro.titulo)
            (activity as? MainActivity)?.replaceFragment(FragmentDetalleLibro.newInstance(libro.titulo))

        }

        recyclerViewCategorias.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCategorias.adapter = categoriaAdapter
    }
}



