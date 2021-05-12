package com.example.hrit_app.services

import android.content.res.Resources
import com.example.hrit_app.entities.User
import com.example.hrit_app.repository.TecnologiaRepository
import com.example.hrit_app.repository.UserRepository

class UserService {

    var tecnologiaRepository: TecnologiaRepository = TecnologiaRepository
    var userRepository: UserRepository = UserRepository

    fun findUserByUsernameAndPassword(email: String, password: String): User {
        try {
            val usuarioFiltrado = userRepository.findByUsernameAndPassword(email, password)
            return usuarioFiltrado
        } catch (e: Resources.NotFoundException) {
            System.out.println(" === ERROR :" + e.message.toString())
            throw e

        }
    }

    fun findAllAsesoresTecnicos(): MutableList<User> {
        return  userRepository.findAllAT()
    }

    fun findByNombre(textNombre: String): MutableList<User> {
        return userRepository.findByTecnologia(textNombre)
    }

    fun createUser(user: User){
        userRepository.save(user)
    }
}