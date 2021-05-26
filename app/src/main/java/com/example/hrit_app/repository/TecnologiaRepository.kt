package com.example.hrit_app.repository

import com.example.hrit_app.R
import com.example.hrit_app.entities.Tecnologia
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object TecnologiaRepository {

    private val db = Firebase.firestore
    val TECNOLOGIA_COLLECTION = "tecnologias";
    val NOMBRE = "nombre"
    val ACTIVE = "active"
    val IMG_SRC = "imgsrc"

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

    suspend fun findAll(): MutableList<Tecnologia>{
        val snahpshot = db.collection(TECNOLOGIA_COLLECTION).get().await();
        return listaTecnologia;
    }

    fun findByText(nombreTecnologia: String): Tecnologia{
        val tecnologiaFiltradaPorNombre = listaTecnologia.filter { tecnologia -> tecnologia.text.equals(nombreTecnologia)  }
        return tecnologiaFiltradaPorNombre.get(0)
    }
}