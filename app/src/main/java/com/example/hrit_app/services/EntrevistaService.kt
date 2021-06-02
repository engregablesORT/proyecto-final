package com.example.hrit_app.services

import android.content.res.Resources
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.repository.EntrevistaRepository

class EntrevistaService {

    var entrevistaRepository: EntrevistaRepository = EntrevistaRepository

    suspend fun findAllEntrevistasPendientesByDev(devId: String): MutableList<Entrevista> {
        try {
            return entrevistaRepository.findAllEntrevistasPorDev(devId)
        } catch (e: Resources.NotFoundException) {
            System.out.println(" === ERROR :" + e.message.toString())
            throw e
        }
    }

    fun updateEntrevistaEstado(idEntrevista: String, estado: String) {
        entrevistaRepository.updateEntrevistaEstado(idEntrevista, estado)
    }

    fun updateEntrevistaComentarios(idEntrevista: String, comentarios: String) {
        entrevistaRepository.updateEntrevistaComentarios(idEntrevista, comentarios)
    }
}