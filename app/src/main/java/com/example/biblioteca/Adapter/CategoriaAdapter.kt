package com.example.biblioteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.LibrosCategoriaActivity
import com.example.biblioteca.R
import android.content.Context
import com.example.biblioteca.model.CategoriaLibro

class CategoriaAdapter(private val context: Context, private val categorias: List<CategoriaLibro>) :
    RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageCategoria: ImageView = view.findViewById(R.id.imageCategoria)
        val textCategoria: TextView = view.findViewById(R.id.textCategoriaTitulo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.imageCategoria.setImageResource(categoria.imagenResId)
        holder.textCategoria.text = categoria.nombre

        holder.itemView.setOnClickListener {
            val intent = LibrosCategoriaActivity.crearIntent(context, categoria.nombre)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = categorias.size
}