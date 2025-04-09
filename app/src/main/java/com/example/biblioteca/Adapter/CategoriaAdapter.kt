package com.example.biblioteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.model.CategoriaLibro
import com.example.biblioteca.model.Libro

class CategoriaAdapter(
    private val categorias: List<CategoriaLibro>,
    private val onLibroClick: (Libro) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.textTituloCategoria.text = categoria.nombre

        val layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerLibros.layoutManager = layoutManager

        val adapter = LibroAdapter(categoria.libros, onLibroClick) // ✅ ahora le pasamos el click
        holder.recyclerLibros.adapter = adapter
    }

    override fun getItemCount(): Int {
        return categorias.size
    }

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTituloCategoria: TextView = itemView.findViewById(R.id.textCategoriaTitulo)
        val recyclerLibros: RecyclerView = itemView.findViewById(R.id.recyclerLibrosCategoria)
    }
}
