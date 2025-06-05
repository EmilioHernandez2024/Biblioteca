package com.example.biblioteca.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.`1HomeActivity`
import com.example.biblioteca.R
import com.example.biblioteca.adapter.CategoriaAdapter
import com.example.biblioteca.adapter.LibroAdapter
import com.example.biblioteca.utils.LibroData

/**
 *  fragmento principal que actúa como la pantalla de inicio de la aplicación.
 * Muestra una sección de libros recientes y otra con categorías de libros.
 */
class Home : Fragment() {

    // Declaración de los RecyclerViews y sus adaptadores.
    private lateinit var recyclerViewRecientes: RecyclerView // RecyclerView para mostrar los libros recientes.
    private lateinit var recyclerViewCategorias: RecyclerView // RecyclerView para mostrar las categorías.
    private lateinit var libroAdapter: LibroAdapter // Adaptador para los libros recientes.
    private lateinit var categoriaAdapter: CategoriaAdapter // Adaptador para las categorías.


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Se llama inmediatamente después de que la vista del fragmento ha sido creada.
     * Aquí se inicializan las vistas y se configuran los RecyclerViews y sus adaptadores.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerViewRecientes = view.findViewById(R.id.recyclerLibrosRecientes)
        recyclerViewCategorias = view.findViewById(R.id.recyclerCategorias)

        // --- Configuración del RecyclerView para libros recientes ---
        // Inicializa el LibroAdapter con la lista de libros recientes de LibroData.
        // Se le pasa una función lambda que se ejecuta cuando se hace clic en un libro.
        libroAdapter = LibroAdapter(LibroData.librosRecientes) { libro ->
            // Crea una nueva instancia del fragmento de detalle del libro.
            val fragment = FragmentDetalleLibro.newInstance(libro)
            // Llama a la función `replaceFragment` de la actividad principal (HomeActivity)
            // para mostrar el fragmento de detalle del libro. El `as? HomeActivity` es un
            // "safe cast" para asegurar que `activity` es una HomeActivity antes de intentar llamar al método.
            (activity as? `1HomeActivity`)?.replaceFragment(FragmentDetalleLibro.newInstance(libro))
        }


        recyclerViewRecientes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewRecientes.adapter = libroAdapter // Asigna el adaptador al RecyclerView de recientes.

        // --- Configuración del RecyclerView para categorías ---
        // Inicializa el CategoriaAdapter con la lista de categorías de LibroData.
        // También se le pasa una función lambda que se ejecuta cuando se hace clic en un libro DENTRO de una categoría.

        categoriaAdapter = CategoriaAdapter(LibroData.categorias) { libro ->

            // Similar al caso de libros recientes, reemplaza el fragmento actual por el de detalle del libro.
            val fragment = FragmentDetalleLibro.newInstance(libro)
            (activity as? `1HomeActivity`)?.replaceFragment(FragmentDetalleLibro.newInstance(libro))
        }

        // Configura el LayoutManager para el RecyclerView de categorías.
        // Se establece como vertical, mostrando cada categoría como una fila.
        recyclerViewCategorias.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCategorias.adapter = categoriaAdapter // Asigna el adaptador al RecyclerView de categorías.
    }
}
