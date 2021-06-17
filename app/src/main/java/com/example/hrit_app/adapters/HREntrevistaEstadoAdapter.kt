package com.example.hrit_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
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

    private lateinit var view: View
    private lateinit var context: Context
    private var userService = UserService()
    private lateinit var userDev: User

    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + parentJob)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HREntrevistaAceptadaAdapterHolder {
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendariohr_estado, parent, false)
        context = parent.context

        return HREntrevistaAceptadaAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: HREntrevistaAceptadaAdapterHolder, position: Int) {
        // Colores de las cards y visibilidad de botones
        when (entrevistasList[position].estado) {
            Entrevista.Constants.estadoAceptado -> {
                view.findViewById<Button>(R.id.calendarhr_contacto).visibility = View.VISIBLE
            }
            Entrevista.Constants.estadoRechazada -> {
                view.findViewById<TextView>(R.id.calendarhr_duracion).visibility = View.INVISIBLE
                view.findViewById<TextView>(R.id.calendarhr_comentarios).visibility = View.VISIBLE
                view.findViewById<ConstraintLayout>(R.id.calendarhr_cl).background =
                    ContextCompat.getDrawable(context, R.color.entrevistas_rechazadas)
            }
            Entrevista.Constants.estadoPendienteRespuesta -> {
                view.findViewById<Button>(R.id.calendarhr_cancelar).visibility = View.VISIBLE
                view.findViewById<ConstraintLayout>(R.id.calendarhr_cl).background =
                    ContextCompat.getDrawable(context, R.color.entrevistas_pendientes)
            }

            Entrevista.Constants.estadoFinalizada -> {
                view.findViewById<ConstraintLayout>(R.id.calendarhr_cl).background =
                    ContextCompat.getDrawable(context, R.color.entrevistas_completadas)
            }

            Entrevista.Constants.estadoCancelada -> {
                view.findViewById<ConstraintLayout>(R.id.calendarhr_cl).background =
                    ContextCompat.getDrawable(context, R.color.entrevistas_canceladas)
            }
        }

        holder.setFecha(entrevistasList[position].fecha)
        holder.setHora(entrevistasList[position].duracion)
        holder.setNombreDev(entrevistasList[position].nombreDev)

        when (entrevistasList[position].estado) {
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


        fun setFecha(fecha: String) {
            view.findViewById<TextView>(R.id.calendarhr_fecha).text = fecha
        }

        fun setComentarios(comentarios: String) {
            view.findViewById<TextView>(R.id.calendarhr_comentarios).text = comentarios
        }

        fun botonContacto(): Button {
            return view.findViewById(R.id.calendarhr_contacto)
        }

        fun botonCancelar(): Button {
            return view.findViewById(R.id.calendarhr_cancelar)
        }

        @SuppressLint("SetTextI18n")
        fun setHora(duracion: Int) {
            view.findViewById<TextView>(R.id.calendarhr_duracion).text =
                "Duracion: $duracion ${validarHoras(duracion)}"
        }

        @SuppressLint("SetTextI18n")
        fun setNombreDev(nombreDev: String) {
            view.findViewById<TextView>(R.id.calendarhr_nombreDev).text = nombreDev
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
