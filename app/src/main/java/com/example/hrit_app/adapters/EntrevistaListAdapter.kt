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
        val onItemClickA: (Int) -> Boolean,
        val onItemClickR: (Int) -> Boolean
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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntrevistaHolder {
        // Aca es donde referencio los items
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entrevista_pendiente, parent, false)
        return (EntrevistaListAdapter.EntrevistaHolder(view))
    }

    override fun getItemCount(): Int {
        return entrevistasList.size
    }

    override fun onBindViewHolder(holder: EntrevistaHolder, position: Int) {
        // Aca mi informacion que seria el nombre de la pelicula, impacta en el holder y lo muestro
        holder.setFecha(entrevistasList[position].fecha)
        holder.setNombreHr(entrevistasList[position].nombreUserHr)
        holder.setEmpresaHr(entrevistasList[position].nombreEmpresaHr)
        holder.getbotonAceptar().setOnClickListener(){
            onItemClickA(position)
        }
        holder.getbotonRechazar().setOnClickListener(){
            onItemClickR(position)
        }

    }
}