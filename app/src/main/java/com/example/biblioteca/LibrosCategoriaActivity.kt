package com.example.biblioteca

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblioteca.adapter.LibroAdapter
import com.example.biblioteca.model.Libro

class LibrosCategoriaActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_CATEGORIA = "categoria"

        fun crearIntent(context: Context, categoria: String): Intent {
            val intent = Intent(context, LibrosCategoriaActivity::class.java)
            intent.putExtra(EXTRA_CATEGORIA, categoria)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_libros_categoria)

        val categoria = intent.getStringExtra(EXTRA_CATEGORIA)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerLibrosCategoria)
        val btnVolverHome = findViewById<Button>(R.id.btnVolverHome)
        val textTituloCategoria = findViewById<TextView>(R.id.textTituloCategoria) // Obtener referencia al TextView

        val libros = obtenerLibrosPorCategoria(categoria ?: "")

        // Mostrar libros SIN bucle
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = LibroAdapter(libros, loop = false)

        // Establecer el título de la categoría
        textTituloCategoria.text = categoria

        // Agregar OnClickListener al botón
        btnVolverHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun obtenerLibrosPorCategoria(categoria: String): List<Libro> {
        val todosLosLibros = listOf(
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Matemática III", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Cálculo II", R.drawable.ic_launcher_foreground, "Ingeniería"),
            Libro("Introducción a Java", R.drawable.ic_launcher_foreground, "Programación"),
            Libro("Android Studio Básico", R.drawable.ic_launcher_foreground, "Programación"),
            Libro("Física I", R.drawable.ic_launcher_foreground, "Física"),
            Libro("Termodinámica", R.drawable.ic_launcher_foreground, "Física")
        )
        return todosLosLibros.filter { it.categoria == categoria }
    }
}
