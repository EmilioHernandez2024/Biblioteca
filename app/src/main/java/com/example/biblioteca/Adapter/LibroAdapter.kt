package com.example.biblioteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.biblioteca.R


/**
 * Adaptador para un RecyclerView que muestra una lista de libros.
 * Utilizado para mostrar libros de una categoría específica o resultados de búsqueda.
 */
class LibroAdapter(
    private var libros: List<Libro>, // Lista de libros a mostrar.
    private val onLibroClick: (Libro) -> Unit // Función para manejar el clic en un libro.
) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    /**
     * Vincula los datos de un libro a la vista de su ViewHolder.
     * Carga la imagen del libro usando Glide y establece un listener de clic.
     */
    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        holder.bind(libro) // Configura la vista con los datos del libro.
        holder.itemView.setOnClickListener {
            onLibroClick(libro) // Invoca la función de clic cuando se toca el elemento.
        }
    }

    /**
     * Retorna el número total de elementos (libros) en el conjunto de datos.
     */
    override fun getItemCount(): Int = libros.size

    /**
     * Actualiza la lista de libros del adaptador y notifica al RecyclerView para que se redibuje.
     */
    fun updateLista(nuevaLista: List<Libro>) {
        libros = nuevaLista
        notifyDataSetChanged() // Informa al adaptador que los datos han cambiado.
    }

    /**
     * ViewHolder para representar la vista de un elemento de libro.
     * Contiene el TextView para el título y el ImageView para la imagen.
     */
    class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloLibro: TextView = itemView.findViewById(R.id.textTitulo)
        private val imagenLibro: ImageView = itemView.findViewById(R.id.imageLibro)

        /**
         * Método para configurar los datos de un libro en los elementos de la vista.
         * Carga la imagen usando Glide con placeholders y manejo de errores.
         */
        fun bind(libro: Libro) {
            tituloLibro.text = libro.titulo
            Glide.with(itemView.context)
                .load(libro.imagen) // URL de la imagen del libro.
                .placeholder(R.drawable.ic_launcher_foreground) // Imagen temporal mientras carga.
                .error(R.drawable.ic_visibility) // Imagen si hay un error al cargar.
                .into(imagenLibro) // ImageView donde se cargará la imagen.
        }
    }
}
