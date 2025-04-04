package com.example.biblioteca.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.adapter.LibroAdapter
import com.example.biblioteca.model.Libro

class LibrosCategoriaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var libros: List<Libro>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_libros_categoria, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerLibrosCategoria)

        // Obtener la lista de libros desde los argumentos
        libros = arguments?.getParcelableArrayList("libros") ?: emptyList()

        // Configurar el RecyclerView con GridLayoutManager
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // Cambia a 3 si quieres 3 columnas
        recyclerView.adapter = LibroAdapter(libros)
    }

    companion object {
        fun newInstance(libros: List<Libro>): LibrosCategoriaFragment {
            val fragment = LibrosCategoriaFragment()
            val args = Bundle()
            args.putParcelableArrayList("libros", ArrayList(libros))
            fragment.arguments = args
            return fragment
        }
    }
}


