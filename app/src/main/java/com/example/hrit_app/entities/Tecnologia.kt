package com.example.hrit_app.entities

import android.os.Parcel
import android.os.Parcelable

class Tecnologia(id: Int, text: String): Parcelable {

    var id: Int
    var text: String


    init {
        this.id = id
        this.text = text
    }

    constructor(source: Parcel) : this(
            source.readInt()!!,
            source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(text)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}