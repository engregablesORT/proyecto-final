package com.example.hrit_app.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.*

open class User(
    id: String,
    email: String,
    password: String,
    name: String,
    lastName: String,
    rol: String,
    tecnologias: List<String> = Collections.EMPTY_LIST as List<String>,
    descripcion: String,
    precio: String,
    titulo: String,
    seniority: String,
    empresa: String,
    valoracion: Double
) : Parcelable {

    constructor() : this("", "", "", "", "", "", emptyList(), "", "", "", "", "", 0.0)

    var id: String
    var email: String
    var password: String
    var name: String
    var lastName: String
    var rol: String
    var tecnologias: List<String>
    var descripcion: String
    var precio: String
    var titulo: String
    var seniority: String
    var empresa: String
    var valoracion: Double

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        email = parcel.readString().toString()
        password = parcel.readString().toString()
        name = parcel.readString().toString()
        lastName = parcel.readString().toString()
        rol = parcel.readString().toString()
        tecnologias = parcel.createStringArrayList()!!
        descripcion = parcel.readString().toString()
        precio = parcel.readString().toString()
        titulo = parcel.readString().toString()
        seniority = parcel.readString().toString()
        empresa = parcel.readString().toString()
    }

    init {
        this.id = id
        this.email = email
        this.password = password
        this.name = name
        this.lastName = lastName
        this.rol = rol
        this.tecnologias = tecnologias
        this.descripcion = descripcion
        this.precio = precio
        this.titulo = titulo
        this.seniority = seniority
        this.empresa = empresa
        this.valoracion = valoracion
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(name)
        parcel.writeString(lastName)
        parcel.writeString(rol)
        parcel.writeStringList(tecnologias)
        parcel.writeString(descripcion)
        parcel.writeString(precio)
        parcel.writeString(titulo)
        parcel.writeString(seniority)
        parcel.writeString(empresa)
        parcel.writeDouble(valoracion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}