package com.example.hrit_app.entities

class Tecnologia(imgsrc: Int, text: String, active: Boolean = false) {

    var imgsrc: Int
    var text: String
    var active: Boolean


    init {
        this.imgsrc = imgsrc
        this.text = text
        this.active = active
    }

}