package com.example.hrit_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.entities.Entrevista

class EntrevistaListAdapter (
        private var  entrevistasList: MutableList<Entrevista>,
        val onAceptarEntrevistaClick: (Int) -> Boolean,
        val onRechazarEntrevistaClick: (Int) -> Boolean
) :  RecyclerView.Adapter<EntrevistaListAdapter.EntrevistaHolder>()  {



    class EntrevistaHolder(v: View) : RecyclerView.ViewHolder(v) {
        // Contiene acciones y referencias con respecto a la vista
        // Conexion entre adapter y xml del item
        private var view: View

        init {
            this.view = v
        }

        fun setFecha(fecha: String){
            val txt: TextView = view.findViewById(R.id.fecha)
            txt.text = fecha
        }

        fun setHora(duracion: Int){
            val txt: TextView = view.findViewById(R.id.duracion)
            txt.text = "Duracion: " + duracion.toString() + validarHoras(duracion)
        }

        fun setNombreHr(nombreHr: String){
            val txt: TextView = view.findViewById(R.id.HR)
            txt.text = nombreHr
        }

        fun setEmpresaHr(empresaHr: String){
            val txt: TextView = view.findViewById(R.id.text_empresa)
            txt.text = empresaHr
        }

        fun getbotonAceptar(): Button {
            return view.findViewById(R.id.button_acept)
        }

        fun getbotonRechazar(): Button {
            return view.findViewById(R.id.button_rechazar)
        }

        fun getCardLayout (): CardView {
            // Me posiciono sobre el click especifico de la card
            return view.findViewById(R.id.card_package_item)
        }

        fun validarHoras(duracion: Int): String {
            return if (duracion > 1) {
                " horas"
            } else {
                " hora"
            }
         }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntrevistaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entrevista_pendiente, parent, false)
        return (EntrevistaListAdapter.EntrevistaHolder(view))
    }

    override fun getItemCount(): Int {
        return entrevistasList.size
    }

    override fun onBindViewHolder(holder: EntrevistaHolder, position: Int) {
        holder.setFecha(entrevistasList[position].fecha)
        holder.setHora(entrevistasList[position].duracion)
        holder.setNombreHr(entrevistasList[position].nombreUserHr)
        holder.setEmpresaHr(entrevistasList[position].nombreEmpresaHr)
        holder.getbotonAceptar().setOnClickListener(){
            onAceptarEntrevistaClick(position)
        }
        holder.getbotonRechazar().setOnClickListener(){
            onRechazarEntrevistaClick(position)
        }

    }
}