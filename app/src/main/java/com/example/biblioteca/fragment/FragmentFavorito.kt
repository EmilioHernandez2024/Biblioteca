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
import com.example.biblioteca.utils.FavoritosManager // Gestor para obtener la lista de libros favoritos.

/**
 *  es un fragmento que muestra la lista de libros marcados como favoritos por el usuario actual.
 */
class FragmentFavorito : Fragment() {

    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la lista de libros favoritos.
    private lateinit var adapter: LibroAdapter // Adaptador para el RecyclerView.
    private var listaFavoritos: List<Libro> = emptyList() // Lista de libros favoritos que se mostrarán.


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout `fragment_favorito.xml` para este fragmento.
        return inflater.inflate(R.layout.fragment_favorito, container, false)
    }

    /**
     * Se llama inmediatamente después de que la vista del fragmento ha sido creada.
     * Aquí se inicializan las vistas, se obtienen los favoritos y se configura el RecyclerView.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView = view.findViewById(R.id.recyclerFavoritos)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obtiene el nombre del usuario actual de las SharedPreferences.
        // Este nombre se usa para identificar los favoritos de cada usuario.
        val prefs = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val usuario = prefs.getString("usuario_actual", "usuario") ?: "usuario"

        // Obtiene la lista de libros favoritos para el usuario actual utilizando el FavoritosManager.
        listaFavoritos = FavoritosManager.obtenerFavoritos(requireContext(), usuario)

        // Inicializa el adaptador para el RecyclerView, pasándole la lista de favoritos y una función lambda.
        // La función lambda se ejecuta cuando se hace clic en un libro favorito.

        adapter = LibroAdapter(listaFavoritos) { libro ->

            // Cuando se hace clic en un libro, se crea una instancia de FragmentDetalleLibro
            // y se reemplaza en el contenedor de fragmentos, permitiendo volver atrás.

            val fragment = FragmentDetalleLibro.newInstance(libro)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Reemplaza el fragmento actual con el de detalle.
                .addToBackStack(null) // Añade la transacción a la pila de retroceso.
                .commit() // Confirma la transacción.
        }

        // Asigna el adaptador configurado al RecyclerView.
        recyclerView.adapter = adapter
    }
}
