package com.example.hrit_app.entities

class Tecnologia(imgsrc: Int, text: String, active: Boolean = false, categoria: String) {

    constructor() : this(0, "", false, "")

    var imgsrc: Int
    var text: String
    var active: Boolean
    var categoria: String


    init {
        this.imgsrc = imgsrc
        this.text = text
        this.active = active
        this.categoria = categoria
    }

}