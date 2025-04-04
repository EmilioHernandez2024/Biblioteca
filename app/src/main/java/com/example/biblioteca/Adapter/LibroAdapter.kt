package com.example.biblioteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.model.Libro

class LibroAdapter(private val libros: List<Libro>) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position % libros.size]
        holder.tituloLibro.text = libro.titulo
        holder.imagenLibro.setImageResource(libro.imagenResId)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE // Para simular un carrusel infinito
    }

    class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloLibro: TextView = itemView.findViewById(R.id.textTitulo)
        val imagenLibro: ImageView = itemView.findViewById(R.id.imageLibro)
    }
}
