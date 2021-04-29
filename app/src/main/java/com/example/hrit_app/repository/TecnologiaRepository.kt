package com.example.hrit_app.repository

import com.example.hrit_app.R
import com.example.hrit_app.entities.Tecnologia

object TecnologiaRepository {

    var listaTecnologia: MutableList<Tecnologia> = mutableListOf(
            Tecnologia(R.drawable.angular, "Angular"),
            Tecnologia(R.drawable.react, "React JS"),
            Tecnologia(R.drawable.java, "Java"),
            Tecnologia(R.drawable.js, "Javascript"),
            Tecnologia(R.drawable.php, "PHP"),
            Tecnologia(R.drawable.nodejs, "Node JS"),
            Tecnologia(R.drawable.db, "Base de Datos"),
            Tecnologia(R.drawable.python, "Python")

    )

    fun findAll(): MutableList<Tecnologia>{
        return listaTecnologia;
    }
}