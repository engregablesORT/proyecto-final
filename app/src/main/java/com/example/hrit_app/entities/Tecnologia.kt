package com.example.hrit_app.entities

class Tecnologia(id: Int, text: String, active: Boolean = false) {

    var id: Int
    var text: String
    var active: Boolean


    init {
        this.id = id
        this.text = text
        this.active = active
    }

}