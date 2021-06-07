package com.example.hrit_app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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
    val onClick: (Int) -> Boolean
) : RecyclerView.Adapter<HREntrevistaEstadoAdapter.HREntrevistaAceptadaAdapterHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HREntrevistaAceptadaAdapterHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendariohr_estado, parent, false)

        when (entrevistasList[0].estado) {
            Entrevista.Constants.estadoAceptado -> {
                view.findViewById<ImageButton>(R.id.calendarhr_contacto).visibility = View.VISIBLE
            }
            Entrevista.Constants.estadoRechazada -> {
                view.findViewById<TextView>(R.id.calendarhr_duracion).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.calendarhr_comentarios).visibility = View.VISIBLE
                view.findViewById<ConstraintLayout>(R.id.calendarhr_cl).background =
                    ContextCompat.getDrawable(parent.context, R.drawable.bg_color)
            }
            Entrevista.Constants.estadoPendienteRespuesta -> {
                view.findViewById<ImageButton>(R.id.calendarhr_cancelar).visibility = View.VISIBLE
                view.findViewById<ConstraintLayout>(R.id.calendarhr_cl).background =
                    ContextCompat.getDrawable(parent.context, R.drawable.bg_color2)
            }
        }

        return (HREntrevistaAceptadaAdapterHolder(view))
    }

    override fun onBindViewHolder(holder: HREntrevistaAceptadaAdapterHolder, position: Int) {
        holder.setFecha(entrevistasList[position].fecha)
        holder.setHora(entrevistasList[position].duracion)
        holder.setNombreDev(entrevistasList[position].idUserDev)

        when (entrevistasList[0].estado) {
            Entrevista.Constants.estadoAceptado -> {
                holder.botonContacto().setOnClickListener { onClick(position) }
            }
            Entrevista.Constants.estadoRechazada -> {
                holder.setComentarios(entrevistasList[position].comentarios)
            }
            Entrevista.Constants.estadoPendienteRespuesta -> {
                holder.botonCancelar().setOnClickListener { onClick(position) }
            }
        }
    }

    override fun getItemCount(): Int {
        return entrevistasList.size
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

        fun botonContacto(): ImageButton {
            return view.findViewById(R.id.calendarhr_contacto)
        }

        fun botonCancelar(): ImageButton {
            return view.findViewById(R.id.calendarhr_cancelar)
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
