package com.example.hrit_app.repository

import android.util.Log
import com.example.hrit_app.entities.Entrevista
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object EntrevistaRepository {

    private val db = Firebase.firestore
    private const val ENTREVISTAS_COLLECTION = "entrevistas";
    private const val DURACION = "duracion";
    private const val ID = "id"
    private const val ESTADO = "estado";
    private const val FECHA = "fecha";
    private const val EMPRESA = "nombreEmpresaHR";
    private const val VALORACION = "valoracion";
    private const val COMENTARIOS = "comentarios";
    private const val ID_DEV = "idUserDev";

    fun crearEntrevista(entrevista: Entrevista) {
        val entrevistaFirebase = db.collection(ENTREVISTAS_COLLECTION).add(entrevista)
        entrevistaFirebase.addOnSuccessListener { documentReference ->
            db.collection(ENTREVISTAS_COLLECTION).document(documentReference.id)
                .update(ID, documentReference.id)
        }
    }

    suspend fun findAllEntrevistasPorDev(devId: String): MutableList<Entrevista> {
        val entrevistas: MutableList<Entrevista> = mutableListOf()
        return try {
            val snapshot = db.collection(ENTREVISTAS_COLLECTION).whereEqualTo(
                ESTADO, Entrevista.Constants.estadoPendienteRespuesta
            ).whereEqualTo(
                ID_DEV, devId
            )
                .get().await()
            for (documento in snapshot.documents) {
                documento.toObject<Entrevista>()?.let { entrevistas.add(it) }
            }
            entrevistas
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
            arrayListOf()
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