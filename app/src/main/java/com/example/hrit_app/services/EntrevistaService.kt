package com.example.hrit_app.services

import android.content.res.Resources
import android.util.Log
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.repository.EntrevistaRepository

class EntrevistaService {

    private var entrevistaRepository = EntrevistaRepository

    suspend fun crearEntrevista(entrevista: Entrevista) {
        entrevistaRepository.crearEntrevista(entrevista)
    }

    suspend fun findAllEntrevistasPendientesByDev(devId: String): MutableList<Entrevista> {
        try {
            return entrevistaRepository.findAllEntrevistasPorDev(devId)
        } catch (e: Resources.NotFoundException) {
            Log.d("ERROR", e.message.toString())
            throw e
        }
    }

    suspend fun findAllEntrevistasByHR(hrId: String): MutableList<Entrevista> {
        try {
            return entrevistaRepository.findAllEntrevistasPorHR(hrId)
        } catch (e: Resources.NotFoundException) {
            Log.d("ERROR", e.message.toString())
            throw e
        }
    }

    suspend fun findAllEntrevistas(): MutableList<Entrevista> {
        try {
            return entrevistaRepository.findAllEntrevistas()
        } catch (e: Resources.NotFoundException) {
            Log.d("ERROR", e.message.toString())
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