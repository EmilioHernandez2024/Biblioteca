package com.example.biblioteca.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.*
import android.view.*
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.biblioteca.R
import com.example.biblioteca.model.Libro
import com.example.biblioteca.utils.FavoritosManager
import java.io.File
import java.io.FileOutputStream

class FragmentDetalleLibro : Fragment() {

    companion object {
        private const val ARG_TITULO = "titulo_libro"
        fun newInstance(titulo: String): FragmentDetalleLibro {
            val fragment = FragmentDetalleLibro()
            val args = Bundle()
            args.putString(ARG_TITULO, titulo)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var tvTitulo: TextView
    private lateinit var btnRegresar: Button
    private lateinit var btnFavorito: Button
    private lateinit var btnQuitarFavorito: Button
    private lateinit var btnDescargar: Button
    private lateinit var pdfImage: ImageView
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button

    private var currentPageIndex = 0
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    private var tituloLibro: String? = null
    private val pdfFileName = "Seguridad.pdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tituloLibro = arguments?.getString(ARG_TITULO)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detalle_libro, container, false)

        tvTitulo = view.findViewById(R.id.tvTituloLibro)
        btnRegresar = view.findViewById(R.id.btnRegresar)
        btnFavorito = view.findViewById(R.id.btnFavorito)
        btnQuitarFavorito = view.findViewById(R.id.btnQuitarFavorito)
        btnDescargar = view.findViewById(R.id.pdfPlaceholder)
        pdfImage = view.findViewById(R.id.imageViewPDF)
        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnSiguiente = view.findViewById(R.id.btnSiguiente)

        tvTitulo.text = tituloLibro

        val prefs = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val usuario = prefs.getString("usuario_actual", "usuario") ?: "usuario"
        val libro = Libro(tituloLibro ?: "Sin título", R.drawable.ic_launcher_foreground)
        val esFavorito = FavoritosManager.esFavorito(requireContext(), usuario, libro)

        btnFavorito.visibility = if (esFavorito) View.GONE else View.VISIBLE
        btnQuitarFavorito.visibility = if (esFavorito) View.VISIBLE else View.GONE

        btnFavorito.setOnClickListener {
            FavoritosManager.agregarFavorito(requireContext(), usuario, libro)
            Toast.makeText(requireContext(), "Libro agregado a favoritos", Toast.LENGTH_SHORT).show()
            btnFavorito.visibility = View.GONE
            btnQuitarFavorito.visibility = View.VISIBLE
        }

        btnQuitarFavorito.setOnClickListener {
            FavoritosManager.eliminarFavorito(requireContext(), usuario, libro)
            Toast.makeText(requireContext(), "Libro eliminado de favoritos", Toast.LENGTH_SHORT).show()
            btnFavorito.visibility = View.VISIBLE
            btnQuitarFavorito.visibility = View.GONE
        }

        btnRegresar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnAnterior.setOnClickListener {
            if (currentPageIndex > 0) {
                currentPageIndex--
                showPage(currentPageIndex)
            }
        }

        btnSiguiente.setOnClickListener {
            if (pdfRenderer != null && currentPageIndex < (pdfRenderer!!.pageCount - 1)) {
                currentPageIndex++
                showPage(currentPageIndex)
            }
        }

        openRenderer()
        showPage(currentPageIndex)

        return view
    }

    private fun openRenderer() {
        val file = File(requireContext().cacheDir, pdfFileName)
        if (!file.exists()) {
            requireContext().assets.open(pdfFileName).use { asset ->
                FileOutputStream(file).use { output ->
                    asset.copyTo(output)
                }
            }
        }

        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
    }

    private fun showPage(index: Int) {
        currentPage?.close()
        currentPage = pdfRenderer?.openPage(index)
        currentPage?.let { page ->
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            pdfImage.setImageBitmap(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPage?.close()
        pdfRenderer?.close()
        parcelFileDescriptor?.close()
    }
}
