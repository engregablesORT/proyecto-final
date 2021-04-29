package com.example.hrit_app.repository

import android.content.res.Resources
import com.example.hrit_app.entities.User
import com.example.hrit_app.utils.constants.Rol

object UserRepository {

    var listaUsuarios: MutableList<User> = mutableListOf(
        User("flor@gmail.com", "passwordflor", "Flor", "Garduno", Rol.AT),
        User("juli@gmail.com", "passwordjuli", "Julian", "Grilli", Rol.AT),
        User("fede@gmail.com", "passwordfede", "Federico", "Mateucci", Rol.RH),
        User("mati@gmail.com", "passwordmati", "Matias", "Romera", Rol.RH),
        User("Santiago_56@hotmail.com", "passwordsanti", "Santiago", "Escuder", Rol.AT))

    fun findByUsernameAndPassword(username: String, password: String): User {
        val usuarioFiltrado = listaUsuarios.filter { userRepo ->
            userRepo.email.equals(username) && userRepo.password.equals(password)
        }
        return getResultFromFilter(usuarioFiltrado)
    }

    fun save(user: User){
        listaUsuarios.add(user)
    }

    fun findAllAT(): MutableList<User>{
       val asesoresTecnicos = listaUsuarios.filter { usuario -> usuario.rol.equals(Rol.AT) }
        return asesoresTecnicos.toMutableList()
    }

    fun getResultFromFilter(usuarioFiltrado : List<User>): User{
        if ( usuarioFiltrado.size > 0) {
            return usuarioFiltrado.get(0)
        } else {
            throw Resources.NotFoundException("User no encontrado")
        }
    }
}