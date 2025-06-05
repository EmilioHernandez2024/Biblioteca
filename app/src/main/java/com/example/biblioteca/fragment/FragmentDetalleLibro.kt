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
import com.example.biblioteca.utils.FavoritosManager
import com.example.biblioteca.utils.ZoomableImageView
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FragmentDetalleLibro : Fragment() {

    /**
     * Objeto complementario (companion object) para crear instancias del fragmento.
     */
    companion object {
        private const val ARG_LIBRO = "libro_completo" // Clave para el argumento que contendrá el objeto Libro.


        fun newInstance(libro: Libro): FragmentDetalleLibro {
            val fragment = FragmentDetalleLibro() // Crea una nueva instancia del fragmento.
            val args = Bundle() // Crea un Bundle para almacenar argumentos.
            args.putParcelable(ARG_LIBRO, libro) // Guarda el objeto Libro (que debe ser Parcelable).
            fragment.arguments = args // Asigna el Bundle de argumentos al fragmento.
            return fragment
        }
    }

    // Declaración de las vistas del layout.
    private lateinit var tvTitulo: TextView
    private lateinit var btnRegresar: Button
    private lateinit var btnFavorito: Button
    private lateinit var btnQuitarFavorito: Button
    private lateinit var pdfImage: ZoomableImageView // ImageView que permite hacer zoom.
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var progressBar: ProgressBar // Barra de progreso para indicar carga del PDF.

    // Variables para el manejo del PDF.
    private var currentPageIndex = 0 // Índice de la página actual del PDF mostrada.
    private var pdfRenderer: PdfRenderer? = null // Objeto para renderizar el PDF.
    private var currentPage: PdfRenderer.Page? = null // La página actual del PDF.
    private var parcelFileDescriptor: ParcelFileDescriptor? = null // Descriptor de archivo para el PDF.

    private var libro: Libro? = null // El objeto Libro que se está mostrando.

    /**
     * Se llama cuando el fragmento es creado.
     * Recupera el objeto [Libro] de los argumentos y configura el comportamiento del botón de retroceso.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libro = arguments?.getParcelable(ARG_LIBRO) // Obtiene el objeto Libro de los argumentos.

        // Configura el comportamiento del botón de retroceso del sistema.
        // Cuando se presiona, el fragmento actual es sacado de la pila.
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            parentFragmentManager.popBackStack() // Regresa al fragmento anterior en la pila.
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_detalle_libro, container, false)

        // Inicializa todas las vistas encontrándolas por su ID en el layout inflado.
        tvTitulo = view.findViewById(R.id.tvTituloLibro)
        btnRegresar = view.findViewById(R.id.btnRegresar)
        btnFavorito = view.findViewById(R.id.btnFavorito)
        btnQuitarFavorito = view.findViewById(R.id.btnQuitarFavorito)
        pdfImage = view.findViewById(R.id.imageViewPDF) as ZoomableImageView
        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnSiguiente = view.findViewById(R.id.btnSiguiente)
        progressBar = view.findViewById(R.id.progressBar) // Inicializa la barra de progreso.

        // Configura el botón de descarga del PDF.
        val botonDescargar = view.findViewById<Button>(R.id.pdfPlaceholder)
        botonDescargar.setOnClickListener {
            libro?.let { // Asegura que el objeto libro no sea nulo.
                descargarConDownloadManager(it.pdfUrl, "${it.titulo}.pdf") // Llama a la función de descarga.
            }
        }

        // Si el objeto libro no es nulo, configura los detalles en la UI.
        libro?.let { libro ->
            tvTitulo.text = libro.titulo // Establece el título del libro.

            // Obtiene el usuario actual de las SharedPreferences para gestionar favoritos.
            val prefs = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            val usuario = prefs.getString("usuario_actual", "usuario") ?: "usuario"

            // Verifica si el libro es favorito y ajusta la visibilidad de los botones de favorito.
            val esFavorito = FavoritosManager.esFavorito(requireContext(), usuario, libro)
            btnFavorito.visibility = if (esFavorito) View.GONE else View.VISIBLE
            btnQuitarFavorito.visibility = if (esFavorito) View.VISIBLE else View.GONE

            // Configura el listener para añadir a favoritos.
            btnFavorito.setOnClickListener {
                FavoritosManager.agregarFavorito(requireContext(), usuario, libro)
                Toast.makeText(requireContext(), "Libro agregado a favoritos", Toast.LENGTH_SHORT).show()
                btnFavorito.visibility = View.GONE
                btnQuitarFavorito.visibility = View.VISIBLE
            }

            // Configura el listener para quitar de favoritos.
            btnQuitarFavorito.setOnClickListener {
                FavoritosManager.eliminarFavorito(requireContext(), usuario, libro)
                Toast.makeText(requireContext(), "Libro eliminado de favoritos", Toast.LENGTH_SHORT).show()
                btnFavorito.visibility = View.VISIBLE
                btnQuitarFavorito.visibility = View.GONE
            }

            // Configura el botón para regresar al fragmento anterior.
            btnRegresar.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            // Configura el botón para ir a la página anterior del PDF.
            btnAnterior.setOnClickListener {
                if (currentPageIndex > 0) { // Si no estamos en la primera página.
                    currentPageIndex--
                    showPage(currentPageIndex) // Muestra la página anterior.
                }
            }

            // Configura el botón para ir a la página siguiente del PDF.
            btnSiguiente.setOnClickListener {
                // Si el PDF está cargado y no es la última página.
                if (pdfRenderer != null && currentPageIndex < (pdfRenderer!!.pageCount - 1)) {
                    currentPageIndex++
                    showPage(currentPageIndex) // Muestra la página siguiente.
                }
            }

            // Inicia la descarga y visualización del PDF cuando se carga la vista del fragmento.
            descargarPdfDesdeUrl(libro.pdfUrl)
        }

        return view
    }

    /**
     * Se llama después de que la vista del fragmento ha sido creada.
     * Ajusta la imagen del PDF al centro una vez que la vista está lista.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pdfImage.post { pdfImage.fitToCenter() } // Asegura que la imagen se ajuste después de que se haya medido su tamaño.
    }

    /**
     * Descarga un archivo PDF desde una URL utilizando una conexión HTTP manual
     * y lo abre para su visualización.
     * Muestra/oculta un ProgressBar durante la descarga.
     * @param urlPdf La URL del archivo PDF a descargar.
     */
    private fun descargarPdfDesdeUrl(urlPdf: String) {
        progressBar.visibility = View.VISIBLE // Muestra el ProgressBar.

        // Inicia la descarga en un hilo separado para no bloquear la interfaz de usuario.
        Thread {
            try {
                val url = URL(urlPdf)
                val connection = url.openConnection() as HttpURLConnection // Abre una conexión HTTP.
                connection.requestMethod = "GET" // Método de solicitud GET.
                connection.connect() // Establece la conexión.

                // Guarda el archivo descargado en el directorio de caché de la aplicación.
                val file = File(requireContext().cacheDir, "temp_pdf.pdf")
                val output = FileOutputStream(file)
                connection.inputStream.copyTo(output) // Copia el contenido del stream a la salida del archivo.
                output.close() // Cierra el stream de salida.

                // Una vez descargado, abre el PDF y lo muestra en el hilo principal (UI thread).
                activity?.runOnUiThread {
                    openRenderer(file) // Abre el renderizador de PDF con el archivo descargado.
                    showPage(currentPageIndex) // Muestra la primera página (o la página actual).
                    progressBar.visibility = View.GONE // Oculta el ProgressBar.
                }

            } catch (e: Exception) {
                e.printStackTrace() // Imprime el stack trace del error para depuración.
                // Muestra un Toast y oculta el ProgressBar en caso de error.
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error al cargar el PDF", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
        }.start() // Inicia el nuevo hilo.
    }

    /**
     * Descarga un archivo PDF utilizando el servicio DownloadManager del sistema Android.
     * Esto permite que la descarga se maneje en segundo plano y muestre notificaciones.
     */
    private fun descargarConDownloadManager(urlPdf: String, nombreArchivo: String) {
        // Crea una solicitud de descarga.
        val request = DownloadManager.Request(Uri.parse(urlPdf)).apply {
            setTitle("Descargando $nombreArchivo") // Título que se muestra en la notificación.
            setDescription("Descargando PDF...") // Descripción en la notificación.
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Notificación visible al completar.
            // Establece el directorio de destino público (carpeta de Descargas).
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombreArchivo)
            setMimeType("application/pdf")
            setAllowedOverMetered(true) // Permitir descarga con datos móviles.
            setAllowedOverRoaming(true) // Permitir descarga en roaming.
        }

        // Obtiene una instancia del DownloadManager y encola la solicitud.
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request) // Añade la solicitud a la cola de descargas.

        Toast.makeText(requireContext(), "Descarga iniciada", Toast.LENGTH_SHORT).show() // Muestra un mensaje al usuario.
    }

    /**
     * Abre un archivo PDF usando [PdfRenderer] para prepararlo para la visualización.
     * @param file El archivo PDF a abrir.
     */
    private fun openRenderer(file: File) {
        // Abre el archivo en modo de solo lectura.
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        // Crea una nueva instancia de PdfRenderer con el descriptor del archivo.
        pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
    }

    /**
     * Muestra una página específica del PDF en el [ZoomableImageView].
     * @param index El índice de la página a mostrar (0-based).
     */
    private fun showPage(index: Int) {
        currentPage?.close() // Cierra la página actualmente abierta para liberar recursos.
        // Abre la página deseada del PDF.
        currentPage = pdfRenderer?.openPage(index)
        currentPage?.let { page ->
            // Crea un Bitmap con las dimensiones de la página del PDF.
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            // Renderiza el contenido de la página en el Bitmap.
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            pdfImage.setImageBitmap(bitmap) // Establece el Bitmap en la ImageView.
            pdfImage.fitToCenter() // Ajusta la imagen al centro.
        }
    }

    /**
     * Se llama cuando el fragmento está a punto de ser destruido.
     * Libera los recursos asociados al renderizador de PDF para evitar fugas de memoria.
     */
    override fun onDestroy() {
        super.onDestroy()
        currentPage?.close() // Cierra la página actual del PDF.
        pdfRenderer?.close() // Cierra el renderizador de PDF.
        parcelFileDescriptor?.close() // Cierra el descriptor del archivo PDF.
    }
}