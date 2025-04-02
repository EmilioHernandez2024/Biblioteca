package com.example.biblioteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.model.Libro

class LibroAdapter(private val listaLibros: List<Libro>) :
    RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.textTitulo)
        val imagen: ImageView = view.findViewById(R.id.imageLibro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = listaLibros[position]
        holder.titulo.text = libro.titulo
        holder.imagen.setImageResource(libro.imagen)
    }

    override fun getItemCount() = listaLibros.size
}
