package com.example.biblioteca.model

import android.os.Parcel
import android.os.Parcelable
import com.example.biblioteca.R

data class Libro(
    val titulo: String,
    val imagenResId: Int = R.drawable.ic_launcher_foreground,
    val categoria: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(titulo)
        parcel.writeInt(imagenResId)
        parcel.writeString(categoria)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Libro> {
        override fun createFromParcel(parcel: Parcel): Libro {
            return Libro(parcel)
        }

        override fun newArray(size: Int): Array<Libro?> {
            return arrayOfNulls(size)
        }
    }
}
