package com.example.hrit_app.entities

import android.os.Parcel
import android.os.Parcelable
import java.util.*


open class User(email: String, password: String, name: String, lastName: String, rol: String,
                tecnologias: List<Tecnologia> = Collections.EMPTY_LIST as List<Tecnologia>){

    var email: String
    var password: String
    var name: String
    var lastName: String
    var rol: String
    var tecnologias: List<Tecnologia>


    init {
        this.email = email
        this.password = password
        this.name = name
        this.lastName = lastName
        this.rol = rol
        this.tecnologias = tecnologias
    }



}