package com.example.hrit_app.repository

import android.content.res.Resources
import com.example.hrit_app.R
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.utils.constants.Rol
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

object UserRepository {

    private val db = Firebase.firestore

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

    fun crearUsuarioFirebase(user: User, uid: String){
        // Create a new user with a first and last name
        val userFirebase = hashMapOf(
            "name" to user.name,
            "lastname" to user.lastName,
            "email" to user.email,
            "passoword" to user.password,
            "rol" to user.rol
        )
        db.collection("users")
            .add(userFirebase)
            .addOnSuccessListener { documentReference ->
                System.out.println("todo ok")
            }
            .addOnFailureListener { e ->
                System.out.println("error")
            }
    }

    fun save(user: User) {
        listaUsuarios.add(user)
    }

    fun delete(user: User) {
        this.listaUsuarios.remove(user)
    }

    fun findAllAT(): MutableList<User> {
        val asesoresTecnicos = listaUsuarios.filter { usuario -> usuario.rol.equals(Rol.AT) }
        return asesoresTecnicos.toMutableList()
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