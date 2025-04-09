package com.example.biblioteca.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.adapter.LibroAdapter
import com.example.biblioteca.utils.LibroData
import java.text.Normalizer

class FragmentBuscar : Fragment(R.layout.fragment_buscar) {

    private lateinit var etBuscar: EditText
    private lateinit var recyclerResultados: RecyclerView
    private lateinit var adapter: LibroAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etBuscar = view.findViewById(R.id.etBuscar)
        recyclerResultados = view.findViewById(R.id.recyclerResultados)

        val todosLosLibros = LibroData.todosLosLibros.toMutableList()

        adapter = LibroAdapter(todosLosLibros) { libro ->
            val fragment = FragmentDetalleLibro.newInstance(libro.titulo)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerResultados.layoutManager = LinearLayoutManager(requireContext())
        recyclerResultados.adapter = adapter

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtro = normalizarTexto(s.toString())
                val filtrados = todosLosLibros.filter {
                    normalizarTexto(it.titulo).contains(filtro)
                }
                adapter.updateLista(filtrados)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // ✅ Esta función elimina acentos y pone todo en minúscula para comparar mejor
    private fun normalizarTexto(texto: String): String {
        return Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}
