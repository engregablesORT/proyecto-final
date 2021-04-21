package com.example.hrit_app.entities

import android.os.Parcel
import android.os.Parcelable


open class User(email: String, password: String, name: String, lastName: String): Parcelable {

    var email: String
    var password: String
    var name: String
    var lastName: String


    init {
        this.email = email
        this.password = password
        this.name = name
        this.lastName = lastName
    }

    constructor(source: Parcel) : this(
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
    }

    class Roles {
        companion object {
            val typeHr = "HR"
            val typeDev = "DEV"
        }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

}