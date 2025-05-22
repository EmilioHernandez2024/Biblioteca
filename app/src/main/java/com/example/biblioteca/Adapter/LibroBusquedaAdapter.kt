package com.example.biblioteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.biblioteca.R
import com.example.biblioteca.model.Libro


/**
 * Adaptador similar a [LibroAdapter] pero diseñado específicamente para mostrar resultados de búsqueda de libros.
 * Puede utilizar un layout de elemento diferente (`item_libro_busqueda.xml`).
 */
class LibroBusquedaAdapter(
    private var libros: List<Libro>, // Lista de libros resultados de la búsqueda.
    private val onLibroClick: (Libro) -> Unit // Función para manejar el clic en un libro.
) : RecyclerView.Adapter<LibroBusquedaAdapter.LibroViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro_busqueda, parent, false)
        return LibroViewHolder(view)
    }

    /**
     * Vincula los datos de un libro a la vista de su ViewHolder.
     * Carga la imagen del libro usando Glide y establece un listener de clic.
     */
    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        holder.bind(libro)
        holder.itemView.setOnClickListener {
            onLibroClick(libro)
        }
    }

    /**
     * Retorna el número total de elementos (libros) en el conjunto de datos de búsqueda.
     */
    override fun getItemCount(): Int = libros.size

    /**
     * Actualiza la lista de libros del adaptador de búsqueda y notifica al RecyclerView.
     */
    fun updateLista(nuevaLista: List<Libro>) {
        libros = nuevaLista
        notifyDataSetChanged()
    }

    /**
     * ViewHolder para representar la vista de un elemento de libro en la búsqueda.
     */
    class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloLibro: TextView = itemView.findViewById(R.id.textTitulo)
        private val imagenLibro: ImageView = itemView.findViewById(R.id.imageLibro)

        /**
         * Método para configurar los datos de un libro en los elementos de la vista de búsqueda.
         */
        fun bind(libro: Libro) {
            tituloLibro.text = libro.titulo
            Glide.with(itemView.context)
                .load(libro.imagen)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_visibility)
                .into(imagenLibro)
        }
    }
}