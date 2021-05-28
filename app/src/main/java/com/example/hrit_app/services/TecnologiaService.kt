package com.example.hrit_app.services

import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.repository.TecnologiaRepository

class TecnologiaService {

    var tecnologiaRepository: TecnologiaRepository = TecnologiaRepository

    suspend fun getAllTecnologias(): MutableList<Tecnologia> {
        return tecnologiaRepository.findAll();
    }
}