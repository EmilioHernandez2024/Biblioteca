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
import com.example.biblioteca.adapter.LibroBusquedaAdapter
import com.example.biblioteca.model.Libro
import com.example.biblioteca.utils.LibroData
import java.text.Normalizer
/**
 * es un fragmento que permite a los usuarios buscar libros.
 * Muestra una barra de búsqueda y un RecyclerView para los resultados.
 */
class FragmentBuscar : Fragment(R.layout.fragment_buscar) { // Asocia este fragmento con su layout XML.

    private lateinit var etBuscar: EditText // Campo de texto para que el usuario escriba su búsqueda.
    private lateinit var recyclerResultados: RecyclerView // RecyclerView para mostrar la lista de libros filtrados.
    private lateinit var adapter: LibroBusquedaAdapter // Adaptador para el RecyclerView de resultados de búsqueda.

    /**
     * Se llama inmediatamente después de que la vista del fragmento ha sido creada.
     * Aquí se inicializan las vistas y se configura la lógica de búsqueda.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        etBuscar = view.findViewById(R.id.etBuscar)
        recyclerResultados = view.findViewById(R.id.recyclerResultados)

        // Obtiene la lista completa de todos los libros disponibles.
        val todosLosLibros = LibroData.todosLosLibros.toMutableList()


        adapter = LibroBusquedaAdapter(todosLosLibros) { libro ->

            // Cuando un libro es clicado, crea una nueva instancia de FragmentDetalleLibro
            // y la reemplaza en el contenedor de fragmentos, añadiéndola a la pila trasera.

            val fragment = FragmentDetalleLibro.newInstance(libro)

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Reemplaza el fragmento actual.
                .addToBackStack(null) // Permite al usuario volver a este fragmento con el botón atrás.
                .commit() // Confirma la transacción del fragmento.
        }

        // Configura el RecyclerView con un LinearLayoutManager (lista vertical estándar).
        recyclerResultados.layoutManager = LinearLayoutManager(requireContext())
        recyclerResultados.adapter = adapter // Asigna el adaptador al RecyclerView.

        // Añade un TextWatcher al campo de búsqueda para reaccionar a los cambios en el texto.
        etBuscar.addTextChangedListener(object : TextWatcher {

            /**
             * Se llama después de que el texto ha cambiado. Aquí se realiza la lógica de filtrado.
             * @param s El Editable que contiene el texto actual del campo de búsqueda.
             */

            override fun afterTextChanged(s: Editable?) {
                val filtro = normalizarTexto(s.toString()) // Normaliza el texto de búsqueda (quita acentos, minúsculas).
                // Filtra la lista de todos los libros.
                // Un libro se incluye si su título o categoría (normalizados) contienen el texto del filtro.
                val filtrados = todosLosLibros.filter {
                    normalizarTexto(it.titulo).contains(filtro) ||
                            normalizarTexto(it.categoria ?: "").contains(filtro) // Maneja categoría nula.
                }
                adapter.updateLista(filtrados) // Actualiza la lista de libros en el adaptador con los resultados filtrados.
            }

            /** No necesitamos implementar lógica para estos métodos en este caso. */
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /**
     * Normaliza un texto para mejorar la búsqueda, eliminando acentos y convirtiendo a minúsculas.
     * @param texto El texto a normalizar.
     * @return El texto normalizado.
     */
    private fun normalizarTexto(texto: String): String {
        return Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD) // Convierte a minúsculas y descompone caracteres con acentos.
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "") // Elimina los caracteres diacríticos (acentos).
    }
}