package com.example.hrit_app.repository

import android.content.res.Resources
import com.example.hrit_app.R
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.utils.constants.Rol
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*

object UserRepository {

    private val db = Firebase.firestore
    val USERS_COLLECTION = "users";
    val NAME = "name"
    val LAST_NAME = "lastname"
    val PASSWORD = "password"
    val EMAIL = "email"
    val TECNOLOGIAS = "TECNOLOGIAS"
    val ROL = "rol"

    var listaUsuarios: MutableList<User> = mutableListOf(
            User("flor@gmail.com", "passwordflor", "Flor", "Garduno", Rol.AT, Arrays.asList(Tecnologia(R.drawable.angular, "Angular"),
                    Tecnologia(R.drawable.java, "Java"), Tecnologia(R.drawable.python, "Python"))),
            User("juli@gmail.com", "passwordjuli", "Julian", "Grilli", Rol.AT, Arrays.asList(Tecnologia(R.drawable.react, "React JS"), Tecnologia(R.drawable.java, "Java"))),
            User("fede@gmail.com", "passwordfede", "Federico", "Mateucci", Rol.RH),
            User("mati@gmail.com", "passwordmati", "Matias", "Romera", Rol.RH),
            User("santi@hotmail.com", "passwordsanti", "Santiago", "Escuder", Rol.AT),
            User("fran@hotmail.com", "passwordsfran", "Francisco", "Heili", Rol.AT),
            User("fabian@hotmail.com", "passwordsfabian", "Fabian", "Pestchanker", Rol.AT),
            User("julian-raspanti@hotmail.com", "passwordsraspanti", "Julian", "Raspanti", Rol.AT))

    fun findByUsernameAndPassword(username: String, password: String): User {
        val usuarioFiltrado = listaUsuarios.filter { userRepo ->
            userRepo.email.equals(username) && userRepo.password.equals(password)
        }
        return getResultFromFilter(usuarioFiltrado)
    }

    fun findByUsername(username: String): User {
        val usuarioFiltrado = listaUsuarios.filter { userRepo ->
            userRepo.email.equals(username)
        }
        return getResultFromFilter(usuarioFiltrado)
    }

    suspend fun  obtenerRolDeUsuarioByEmail(email: String): User? {
       val snapshot = db.collection(USERS_COLLECTION).whereEqualTo(EMAIL, email).get().await()
       val mapa = snapshot.documents.get(0).data

       return User(mapa?.get(EMAIL).toString(), mapa?.get(PASSWORD).toString(),
                mapa?.get(NAME).toString(), mapa?.get(LAST_NAME).toString(), mapa?.get(ROL).toString())
   }

    fun crearUsuarioFirebase(user: User, uid: String){
        var userFirebase : User = User(user.email,user.password, user.name, user.lastName, user.rol)
        db.collection("users").document(uid).set(userFirebase)
    }

    fun save(user: User) {
        listaUsuarios.add(user)
    }

    fun delete(user: User) {
        this.listaUsuarios.remove(user)
    }

    suspend fun findAllAT(): MutableList<User> {
        var usersAT : MutableList<User> = arrayListOf()
        try {
            val snapshot = db.collection(USERS_COLLECTION).whereEqualTo(ROL, Rol.AT).get().await()
            for (documento in snapshot.documents){
                val user = obtenerUsuarioByDocumentoDeFirebase(documento.data as Map<String, Object>)
                usersAT.add(user)
            }
            return usersAT
        } catch (e: Exception) {
            return arrayListOf()
        }
    }

    private fun obtenerUsuarioByDocumentoDeFirebase(mapa: Map<String, Object>): User {
        return User(mapa?.get(EMAIL).toString(), mapa?.get(PASSWORD).toString(),
            mapa?.get(NAME).toString(), mapa?.get(LAST_NAME).toString(), mapa?.get(ROL).toString())
    }

    fun getResultFromFilter(usuarioFiltrado: List<User>): User {
        if (usuarioFiltrado.size > 0) {
            return usuarioFiltrado.get(0)
        }
        throw Resources.NotFoundException("User no encontrado")

    }

    fun findByTecnologia(text: String): MutableList<User> {
        val usuariosFiltrados = listaUsuarios.filter { usuario ->
            usuario.rol.equals(Rol.AT) && (usuario.name.toUpperCase().contains(text.toUpperCase()) ||
                    usuario.lastName.toUpperCase().contains(text.toUpperCase()))
        }
        return usuariosFiltrados.toMutableList()
    }
}