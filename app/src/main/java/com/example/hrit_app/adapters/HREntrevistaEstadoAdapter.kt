package com.example.hrit_app.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HREntrevistaEstadoAdapter(
    private var entrevistasList: MutableList<Entrevista>,
    val onContactoClick: (Int) -> Boolean
) : RecyclerView.Adapter<HREntrevistaEstadoAdapter.HREntrevistaAceptadaAdapterHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HREntrevistaAceptadaAdapterHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendariohr_estado, parent, false)

        if (esEntrevistaAceptada()) {
            view.findViewById<ImageButton>(R.id.calendarhr_contacto).visibility = View.VISIBLE
        }

        if (esEntrevistaRechazada()) {
            view.findViewById<TextView>(R.id.calendarhr_comentarios).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.calendarhr_duracion).visibility = View.INVISIBLE
            view.findViewById<ConstraintLayout>(R.id.calendarhr_cl).background = ContextCompat.getDrawable(parent.context, R.drawable.bg_color)
        }

        return (HREntrevistaAceptadaAdapterHolder(view))
    }

    override fun onBindViewHolder(holder: HREntrevistaAceptadaAdapterHolder, position: Int) {
        holder.setFecha(entrevistasList[position].fecha)
        holder.setHora(entrevistasList[position].duracion as Int)
        holder.setNombreDev(entrevistasList[position].idUserDev)

        if (esEntrevistaAceptada()) {
            holder.botonContacto(entrevistasList[position].idUserDev).setOnClickListener {
                onContactoClick(position)
            }
        } else if (esEntrevistaRechazada()) {
            holder.setComentarios(entrevistasList[position].comentarios)
        }
    }

    override fun getItemCount(): Int {
        return entrevistasList.size
    }

    private fun esEntrevistaAceptada(): Boolean {
        return entrevistasList[0].estado == Entrevista.Constants.estadoAceptado
    }

    private fun esEntrevistaRechazada(): Boolean {
        return entrevistasList[0].estado == Entrevista.Constants.estadoRechazada
    }

    // Holder
    class HREntrevistaAceptadaAdapterHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var userService = UserService()
        private lateinit var userDev: User

        private val parentJob = Job()
        private val scope = CoroutineScope(Dispatchers.Default + parentJob)

        fun setFecha(fecha: String) {
            view.findViewById<TextView>(R.id.calendarhr_fecha).text = fecha
        }

        fun setComentarios(comentarios: String) {
            view.findViewById<TextView>(R.id.calendarhr_comentarios).text = comentarios
        }

        fun botonContacto(idDev: String): ImageButton {
            return view.findViewById(R.id.calendarhr_contacto)
        }

        @SuppressLint("SetTextI18n")
        fun setHora(duracion: Int) {
            view.findViewById<TextView>(R.id.calendarhr_duracion).text =
                "Duracion: $duracion ${validarHoras(duracion)}"
        }

        @SuppressLint("SetTextI18n")
        fun setNombreDev(idDev: String) {
            scope.launch {
                userDev = userService.findByID(idDev)!!
                view.findViewById<TextView>(R.id.calendarhr_nombreDev).text =
                    "${userDev.name} ${userDev.lastName}"
            }
        }

        private fun validarHoras(duracion: Int): String {
            return if (duracion > 1) {
                "horas"
            } else {
                "hora"
            }
        }

    }
}
