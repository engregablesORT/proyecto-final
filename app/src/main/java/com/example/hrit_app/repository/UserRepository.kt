package com.example.hrit_app.repository

import android.content.res.Resources
import android.sax.RootElement
import android.util.Log
import com.example.hrit_app.R
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.utils.constants.Rol
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

object UserRepository {

    private val db = Firebase.firestore
    val USERS_COLLECTION = "users";
    val NAME = "name"
    val LAST_NAME = "lastName"
    val PASSWORD = "password"
    val EMAIL = "email"
    val ROL = "rol"
    val DESCRIPCION = "descripcion"
    val PRECIO = "precio"
    val TITULO = "titulo"
    val SENIORITY = "seniority"
    val TECNOLOGIAS = "tecnologias"




    suspend fun obtenerUsuarioByEmail(email: String): User? {
        val snapshot = db.collection(USERS_COLLECTION).whereEqualTo(EMAIL, email).get().await()
        return snapshot.documents[0].toObject<User>()
    }

    fun crearUsuarioFirebase(user: User, uid: String) {
        var userFirebase: User =
            User(
                user.email,
                user.password,
                user.name,
                user.lastName,
                user.rol,
                emptyList(),
                "",
                "",
                "",
                ""
            )
        db.collection("users").document(uid).set(userFirebase)
    }

    suspend fun findAllAT(): MutableList<User> {
        var usersAT: MutableList<User> = arrayListOf()
        try {
            val snapshot = db.collection(USERS_COLLECTION).whereEqualTo(ROL, Rol.AT).get().await()
            for (documento in snapshot.documents) {
                // val user =
                //     obtenerUsuarioByDocumentoDeFirebase(documento.data as Map<String, Object>)
                val user = documento.toObject<User>()
                if (user != null) {
                    usersAT.add(user)
                }
            }
            return usersAT
        } catch (e: Exception) {
            return arrayListOf()
        }
    }

    fun update(user: User, uid: String) {
        db.collection(USERS_COLLECTION).document(uid)
            .update(
                "email",
                user.email,
                "name",
                user.name,
                "lastName",
                user.lastName,
                "password",
                user.password
            )
    }


    fun updateAsesor(user: User, uid: String){
        db.collection(USERS_COLLECTION).document(uid)
            .update(
                EMAIL, user.email,
                NAME, user.name,
                LAST_NAME, user.lastName,
                PASSWORD, user.password,
                DESCRIPCION, user.descripcion,
                PRECIO, user.precio,
                TITULO, user.titulo,
                ROL, user.rol,
                SENIORITY, user.seniority
            )
    }
}