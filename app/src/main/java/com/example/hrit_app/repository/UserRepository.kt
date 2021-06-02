package com.example.hrit_app.repository

import android.util.Log
import com.example.hrit_app.entities.User
import com.example.hrit_app.utils.constants.Rol
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object UserRepository {

    private val db = Firebase.firestore
    private const val USERS_COLLECTION = "users";
    private const val NAME = "name"
    private const val LAST_NAME = "lastName"
    private const val PASSWORD = "password"
    private const val EMAIL = "email"
    private const val ID = "id"
    private const val ROL = "rol"
    private const val DESCRIPCION = "descripcion"
    private const val PRECIO = "precio"
    private const val TITULO = "titulo"
    private const val EMPRESA = "empresa"
    private const val SENIORITY = "seniority"
    private const val TECNOLOGIAS = "tecnologias"

    suspend fun obtenerUsuarioByEmail(email: String): User? {
        val snapshot = db.collection(USERS_COLLECTION).whereEqualTo(EMAIL, email).get().await()
        return snapshot.documents[0].toObject<User>()
    }

    suspend fun obtenerUsuarioByID(idUser: String): User? {
        val snapshot = db.collection(USERS_COLLECTION).whereEqualTo(ID, idUser).get().await()
        return snapshot.documents[0].toObject<User>()
    }

    fun crearUsuarioFirebase(user: User, uid: String) {
        var userFirebase: User =
            User(
                user.id,
                user.email,
                user.password,
                user.name,
                user.lastName,
                user.rol,
                emptyList(),
                "",
                "",
                "",
                "",
                ""
            )
        db.collection("users").document(uid).set(userFirebase)
    }

    suspend fun findAllAT(): MutableList<User> {
        val usersAT: MutableList<User> = arrayListOf()
        return try {
            val snapshot = db.collection(USERS_COLLECTION).whereEqualTo(ROL, Rol.AT).get().await()
            for (documento in snapshot.documents) {
                documento.toObject<User>()?.let { usersAT.add(it) }
            }
            usersAT
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            arrayListOf()
        }
    }

    fun updateUserHR(user: User, uid: String) {
        db.collection(USERS_COLLECTION).document(uid)
            .update(
                NAME,
                user.name,
                LAST_NAME,
                user.lastName,
                PASSWORD,
                user.password,
                EMAIL,
                user.email,
                TITULO,
                user.titulo,
                EMPRESA,
                user.empresa
            )
    }

    fun updateAsesor(user: User, uid: String) {
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