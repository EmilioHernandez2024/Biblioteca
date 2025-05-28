package com.example.biblioteca.model

import android.os.Parcel
import android.os.Parcelable
import com.example.biblioteca.R

/**
 * **`Libro` es una clase de datos (data class) que representa la estructura de un libro.**
 * Contiene propiedades clave como el título, las URLs del PDF y la imagen, y una categoría opcional.
 *
 * Implementa la interfaz `Parcelable` para que los objetos `Libro` puedan ser
 * **pasados de forma eficiente entre diferentes componentes de Android**, como
 * entre actividades o fragmentos, a través de `Bundle`s o `Intent`s.
 */
data class Libro(
    val titulo: String, // El título del libro.
    val pdfUrl: String, // La URL directa del archivo PDF del libro.
    val imagen: String, // La URL de la imagen de portada del libro.
    val categoria: String? = null // La categoría a la que pertenece el libro; puede ser nula.
) : Parcelable { // Declara que la clase implementa Parcelable.

    /**
     * **Constructor secundario requerido por `Parcelable`.**
     * Este constructor se usa para recrear un objeto `Libro` a partir de un `Parcel`
     * (un contenedor de datos serializados).
     *
     * @param parcel El `Parcel` del cual se leerán los datos.
     */
    constructor(parcel: Parcel) : this(
        // Lee el título del parcel. Si es nulo, usa una cadena vacía como valor por defecto.
        parcel.readString() ?: "",
        // Lee la URL del PDF. Si es nula, usa una cadena vacía.
        parcel.readString() ?: "",
        // Lee la URL de la imagen. Si es nula, usa una cadena vacía.
        parcel.readString() ?: "",
        // Lee la categoría. Puede ser nula, por eso no se usa `?: ""`.
        parcel.readString()
    )

    /**
     * **Escribe los datos del objeto `Libro` en un `Parcel`.**
     * Este método se llama cuando el objeto necesita ser serializado para pasarlo
     * a otro componente.
     *
     * @param parcel El `Parcel` en el que se escribirán los datos.
     * @param flags Banderas adicionales sobre cómo se debe escribir el objeto.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(titulo) // Escribe el título en el parcel.
        parcel.writeString(pdfUrl) // Escribe la URL del PDF.
        parcel.writeString(imagen) // Escribe la URL de la imagen.
        parcel.writeString(categoria) // Escribe la categoría.
    }

    /**
     * **Describe los tipos de objetos especiales contenidos en la representación `Parcelable` de esta instancia.**
     * En la mayoría de los casos, este método retorna `0`.
     *
     * @return `0` si el objeto no contiene objetos especiales.
     */
    override fun describeContents(): Int = 0

    /**
     * **`CREATOR` es un objeto compañero requerido por `Parcelable`.**
     * Es responsable de generar instancias de la clase `Parcelable` a partir de un `Parcel`.
     */
    companion object CREATOR : Parcelable.Creator<Libro> {
        /**
         * **Crea una nueva instancia de la clase `Parcelable`, inicializándola a partir de los contenidos de un `Parcel`.**
         *
         * @param parcel El `Parcel` desde el que se leerán los datos.
         * @return Una nueva instancia de `Libro`.
         */
        override fun createFromParcel(parcel: Parcel): Libro {
            return Libro(parcel) // Llama al constructor secundario para crear el objeto.
        }

        /**
         * **Crea un nuevo array de la clase `Parcelable`.**
         *
         * @param size El tamaño del array que se creará.
         * @return Un nuevo array de `Libro`s (que pueden ser nulos).
         */
        override fun newArray(size: Int): Array<Libro?> {
            return arrayOfNulls(size) // Retorna un array de `Libro`s de tamaño `size`, inicializados a nulo.
        }
    }
}
