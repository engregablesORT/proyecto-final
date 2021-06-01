package com.example.hrit_app.services

import android.content.res.Resources
import android.util.Log
import com.example.hrit_app.entities.User
import com.example.hrit_app.repository.TecnologiaRepository
import com.example.hrit_app.repository.UserRepository
import com.example.hrit_app.utils.constants.Rol
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class UserService {

    var userRepository: UserRepository = UserRepository

    suspend fun findByEmail(email: String): User? {
        try {
            return userRepository.obtenerUsuarioByEmail(email)
        } catch (e: Resources.NotFoundException) {
            System.out.println(" === ERROR :" + e.message.toString())
            throw e
        }
    }

    suspend fun findByID(idUser: String): User? {
        try {
            return userRepository.obtenerUsuarioByID(idUser)
        } catch (e: Resources.NotFoundException) {
            Log.d("TEST", e.message.toString())
            throw e
        }
    }

    suspend fun findAllAsesoresTecnicos(): MutableList<User> {
        return userRepository.findAllAT()
    }

    fun findByNombre(textNombre: String, asesoresTecnicos:MutableList<User>): MutableList<User> {
        val usuariosFiltrados = asesoresTecnicos.filter { usuario ->
            usuario.rol.equals(Rol.AT) && (usuario.name.toUpperCase().contains(textNombre.toUpperCase()) ||
                    usuario.lastName.toUpperCase().contains(textNombre.toUpperCase()))
        }
        return usuariosFiltrados.toMutableList()
    }


    fun createUserFirebase(user: User, uid: String) {
        userRepository.crearUsuarioFirebase(user, uid)
    }

    fun updateUser(user: User, uid: String){
        userRepository.update(user, uid)
    }

    fun updateAsesorTecnico(user: User, uid: String){
        userRepository.updateAsesor(user, uid);
    }

}