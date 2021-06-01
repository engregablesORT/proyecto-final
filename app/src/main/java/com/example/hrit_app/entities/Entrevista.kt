package com.example.hrit_app.entities

import android.os.Parcel
import android.os.Parcelable

class Entrevista(
    id: String,
    nombreUserHr: String,
    nombreEmpresaHr: String,
    idUserDev: String,
    idUserHr: String,
    fecha: String,
    duracion: Int,
    valoracion: Int,
    precio: Int,
    estado: String,
    comentarios: String
) : Parcelable {

    constructor() : this("", "", "", "", "", "", 0, 0, 0, "", "")

    var id: String
    var nombreUserHr: String
    var nombreEmpresaHr: String
    var idUserDev: String
    var idUserHr: String
    var fecha: String
    var duracion: Int
    var valoracion: Int
    var precio: Int
    var estado: String
    var comentarios: String

    class Constants {
        companion object {
            val estadoAceptado = "ACEPTADO"
            val estadoPendienteRespuesta = "PENDIENTE"
            val estadoRechazada = "RECHAZADA"
            val estadoFinalizada = "FINALIZADA"
        }
    }

    init {
        this.id = id
        this.nombreUserHr = nombreUserHr
        this.nombreEmpresaHr = nombreEmpresaHr
        this.idUserDev = idUserDev
        this.idUserHr = idUserHr
        this.fecha = fecha
        this.duracion = duracion
        this.valoracion = valoracion
        this.precio = precio
        this.estado = estado
        this.comentarios = comentarios
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        nombreUserHr = parcel.readString().toString()
        nombreEmpresaHr = parcel.readString().toString()
        idUserDev = parcel.readString().toString()
        idUserHr = parcel.readString().toString()
        fecha = parcel.readString().toString()
        valoracion = parcel.readInt()
        estado = parcel.readString().toString()
        comentarios = parcel.readString().toString()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombreUserHr)
        parcel.writeString(nombreEmpresaHr)
        parcel.writeString(idUserDev)
        parcel.writeString(idUserHr)
        parcel.writeString(fecha)
        parcel.writeInt(valoracion)
        parcel.writeString(estado)
        parcel.writeString(comentarios)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Entrevista> {
        override fun createFromParcel(parcel: Parcel): Entrevista {
            return Entrevista(parcel)
        }

        override fun newArray(size: Int): Array<Entrevista?> {
            return arrayOfNulls(size)
        }
    }
}