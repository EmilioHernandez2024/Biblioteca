package com.example.biblioteca.model

import android.os.Parcel
import android.os.Parcelable
import com.example.biblioteca.R

data class Libro(
    val titulo: String,
    val imagenResId: Int = R.drawable.ic_launcher_foreground
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(titulo)
        parcel.writeInt(imagenResId)
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
