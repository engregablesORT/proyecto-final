package com.example.hrit_app.entities

class Entrevista (id: Int, nombreUserHr: String , nombreEmpresaHr: String, idUserDev: Int , fecha : String, duracion: Int, valoracion: Int?, estado: String?) {

    var id: Int = 0

    var nombreUserHr: String = ""

    var nombreEmpresaHr: String = ""

    var idUserDev: Int = 0

    var fecha: String = ""

    var duracion: Int = 0

    var urlImage: String = ""

    var valoracion: Int = 0


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
        this.urlImage= urlImage!!
        this.valoracion= valoracion!!
    }
}