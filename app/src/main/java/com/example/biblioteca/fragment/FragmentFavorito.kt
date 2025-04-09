package com.example.biblioteca.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.adapter.LibroAdapter
import com.example.biblioteca.model.Libro
import com.example.biblioteca.utils.FavoritosManager

class FragmentFavorito : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibroAdapter
    private var listaFavoritos: List<Libro> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorito, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerFavoritos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val prefs = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val usuario = prefs.getString("usuario_actual", "usuario") ?: "usuario"

        listaFavoritos = FavoritosManager.obtenerFavoritos(requireContext(), usuario)

        adapter = LibroAdapter(listaFavoritos) { libro ->
            val fragment = FragmentDetalleLibro.newInstance(libro.titulo)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter
    }
}
