package com.example.biblioteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.R
import com.example.biblioteca.model.CategoriaLibro

/**
 * Adaptador para un RecyclerView que muestra una lista de categorías de libros.
 * Cada categoría, a su vez, contiene un RecyclerView horizontal para los libros.
 */

class CategoriaAdapter(
    private val categorias: List<CategoriaLibro>, // Lista de categorías a mostrar.
    private val onLibroClick: (Libro) -> Unit // Función para manejar el clic en un libro individual.
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    /**
     * Crea y devuelve un ViewHolder para la vista de una categoría.
     * Infla el layout `item_categoria.xml`.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    /**
     * Vincula los datos de una categoría a la vista de su ViewHolder.
     * Configura el título de la categoría y el RecyclerView anidado para los libros.
     */
    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.textTituloCategoria.text = categoria.nombre

        // Configura el RecyclerView anidado para mostrar los libros horizontalmente.
        val layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerLibros.layoutManager = layoutManager

        // Crea un LibroAdapter para los libros de esta categoría y le pasa la función de clic.
        val adapter = LibroAdapter(categoria.libros, onLibroClick)
        holder.recyclerLibros.adapter = adapter
    }

    /**
     * Retorna el número total de elementos (categorías) en el conjunto de datos.
     */
    override fun getItemCount(): Int {
        return categorias.size
    }

    /**
     * ViewHolder para representar la vista de un elemento de categoría.
     * Contiene el TextView para el título y el RecyclerView para los libros.
     */
    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTituloCategoria: TextView = itemView.findViewById(R.id.textCategoriaTitulo)
        val recyclerLibros: RecyclerView = itemView.findViewById(R.id.recyclerLibrosCategoria)
    }
}