package com.example.biblioteca.fragment

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.*
import android.view.*
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.biblioteca.R
import com.example.biblioteca.model.Libro
import com.example.biblioteca.utils.FavoritosManager
import com.example.biblioteca.utils.ZoomableImageView
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FragmentDetalleLibro : Fragment() {

    companion object {
        private const val ARG_LIBRO = "libro_completo"

        fun newInstance(libro: Libro): FragmentDetalleLibro {
            val fragment = FragmentDetalleLibro()
            val args = Bundle()
            args.putParcelable(ARG_LIBRO, libro)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var tvTitulo: TextView
    private lateinit var btnRegresar: Button
    private lateinit var btnFavorito: Button
    private lateinit var btnQuitarFavorito: Button
    private lateinit var pdfImage: ZoomableImageView
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var progressBar: ProgressBar // Referencia al ProgressBar

    private var currentPageIndex = 0
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    private var libro: Libro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libro = arguments?.getParcelable(ARG_LIBRO)

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
        pdfImage = view.findViewById(R.id.imageViewPDF) as ZoomableImageView
        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnSiguiente = view.findViewById(R.id.btnSiguiente)
        progressBar = view.findViewById(R.id.progressBar) // Inicializa el ProgressBar

        val botonDescargar = view.findViewById<Button>(R.id.pdfPlaceholder)
        botonDescargar.setOnClickListener {
            libro?.let {
                descargarConDownloadManager(it.pdfUrl, "${it.titulo}.pdf")
            }
        }

        libro?.let { libro ->
            tvTitulo.text = libro.titulo

            val prefs = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            val usuario = prefs.getString("usuario_actual", "usuario") ?: "usuario"

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
                parentFragmentManager.popBackStack()
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

            // Iniciar la descarga del PDF cuando se crea la vista
            descargarPdfDesdeUrl(libro.pdfUrl)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pdfImage.post { pdfImage.fitToCenter() }
    }

    private fun descargarPdfDesdeUrl(urlPdf: String) {
        // Muestra el ProgressBar antes de iniciar la descarga
        progressBar.visibility = View.VISIBLE
        Thread {
            try {
                val url = URL(urlPdf)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val file = File(requireContext().cacheDir, "temp_pdf.pdf")
                val output = FileOutputStream(file)
                connection.inputStream.copyTo(output)
                output.close()

                activity?.runOnUiThread {
                    openRenderer(file)
                    showPage(currentPageIndex)
                    // Oculta el ProgressBar una vez que el PDF se ha cargado
                    progressBar.visibility = View.GONE
                }

            } catch (e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error al cargar el PDF", Toast.LENGTH_SHORT).show()
                    // Oculta el ProgressBar en caso de error
                    progressBar.visibility = View.GONE
                }
            }
        }.start()
    }

    private fun descargarConDownloadManager(urlPdf: String, nombreArchivo: String) {
        val request = DownloadManager.Request(Uri.parse(urlPdf)).apply {
            setTitle("Descargando $nombreArchivo")
            setDescription("Descargando PDF...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombreArchivo)
            setMimeType("application/pdf")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(requireContext(), "Descarga iniciada", Toast.LENGTH_SHORT).show()
    }

    private fun openRenderer(file: File) {
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
            pdfImage.fitToCenter()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPage?.close()
        pdfRenderer?.close()
        parcelFileDescriptor?.close()
    }
}