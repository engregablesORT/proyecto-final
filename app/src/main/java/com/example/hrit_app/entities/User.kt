package com.example.hrit_app.entities

import android.os.Parcel
import android.os.Parcelable


open class User(email: String, password: String, name: String, lastName: String, rol: String): Parcelable {

    var email: String
    var password: String
    var name: String
    var lastName: String
    var rol: String


    init {
        this.email = email
        this.password = password
        this.name = name
        this.lastName = lastName
        this.rol = rol
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(email)
        writeString(password)
        writeString(name)
        writeString(lastName)
        writeString(rol)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

}