package com.example.hrit_app.entities

import com.google.firebase.Timestamp

class Entrevista (id: String, nombreUserHr: String , nombreEmpresaHr: String, idUserDev: String , fecha : String, duracion: Int, valoracion: Int?, estado: String?, comentarios: String?) {

    constructor() : this("", "", "", "", "", 0, 0, "", "")

    var id: String
    var nombreUserHr: String
    var nombreEmpresaHr: String
    var idUserDev: String
    var fecha : String
    var duracion: Int
    var valoracion: Int
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
        this.id = id!!
        this.nombreUserHr = nombreUserHr!!
        this.nombreEmpresaHr = nombreEmpresaHr
        this.idUserDev= idUserDev!!
        this.fecha= fecha!!
        this.duracion= duracion!!
        this.valoracion= valoracion!!
        this.estado= estado!!
        this.comentarios= comentarios!!
    }
}