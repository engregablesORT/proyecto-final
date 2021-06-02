package com.example.hrit_app.repository

import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.entities.User
import com.example.hrit_app.utils.constants.Rol
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object EntrevistaRepository {

    private val db = Firebase.firestore
    const val ENTREVISTAS_COLLECTION = "entrevistas";
    const val DURACION = "duracion_horas";
    const val ESTADO = "estado";
    const val FECHA = "fecha";
    const val EMPRESA = "nombreEmpresaHR";
    const val VALORACION = "valoracion";
    const val COMENTARIOS = "comentarios";
    const val ID_DEV = "idUserDev";

    suspend fun findAllEntrevistasPorDev(devId: String): MutableList<Entrevista> {
        var entrevistas: MutableList<Entrevista> = arrayListOf()
        try {
            val snapshot = db.collection(ENTREVISTAS_COLLECTION).whereEqualTo(
                ESTADO, Entrevista.Constants.estadoPendienteRespuesta).whereEqualTo(
                ID_DEV, devId)
                .get().await()
            for (documento in snapshot.documents) {
                val entrevista = documento.toObject<Entrevista>()
                if (entrevista != null) {
                    entrevistas.add(entrevista)
                }
            }
            return entrevistas
        } catch (e: Exception) {
            return arrayListOf()
        }
    }

    fun updateEntrevistaEstado(entrevistaId: String, estado: String) {
        db.collection(ENTREVISTAS_COLLECTION).document(entrevistaId)
            .update(
                ESTADO,
                estado
            )
    }

    fun updateEntrevistaComentarios(entrevistaId: String, comentarios: String) {
        db.collection(ENTREVISTAS_COLLECTION).document(entrevistaId)
            .update(
                COMENTARIOS,
                comentarios
            )
    }

}