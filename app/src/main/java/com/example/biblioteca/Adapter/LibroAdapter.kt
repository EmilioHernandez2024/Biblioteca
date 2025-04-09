package com.example.biblioteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.model.Libro

class LibroAdapter(
    private var libros: List<Libro>,
    private val onLibroClick: (Libro) -> Unit
) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        holder.bind(libro)
        holder.itemView.setOnClickListener {
            onLibroClick(libro)
        }
    }

    override fun getItemCount(): Int = libros.size

    fun updateLista(nuevaLista: List<Libro>) {
        libros = nuevaLista
        notifyDataSetChanged()
    }

    class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloLibro: TextView = itemView.findViewById(R.id.textTitulo)
        private val imagenLibro: ImageView = itemView.findViewById(R.id.imageLibro)

        fun bind(libro: Libro) {
            tituloLibro.text = libro.titulo
            imagenLibro.setImageResource(libro.imagen)
        }
    }
}
