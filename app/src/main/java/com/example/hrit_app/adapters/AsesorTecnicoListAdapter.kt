package com.example.hrit_app.adapters

import com.example.hrit_app.entities.User
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R

class AsesorTecnicoListAdapter(
    private var asesores: MutableList<User>,
    val onItemClick: (Int) -> Boolean
) : RecyclerView.Adapter<AsesorTecnicoListAdapter.AsesorTecnicoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsesorTecnicoHolder {
        // Aca es donde referencio los items hijos del parent
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_at, parent, false)
        return (AsesorTecnicoListAdapter.AsesorTecnicoHolder(view))
    }

    override fun getItemCount(): Int {
        return asesores.size
    }

    override fun onBindViewHolder(holder: AsesorTecnicoHolder, position: Int) {
        // Aca mi informacion que seria el nombre de la pelicula, impacta en el holder y lo muestro
        holder.setName(asesores[position].name)
        holder.setApellido(asesores[position].lastName)
        holder.getCardLayout().setOnLongClickListener() {
            onItemClick(position)
        }
    }

    class AsesorTecnicoHolder(v: View) : RecyclerView.ViewHolder(v) {
        // Contiene acciones y referencias con respecto a la vista
        // Conexion entre adapter y xml del item
        private var view: View

        init {
            this.view = v
        }

        fun setName(nombre: String) {
            val name: TextView = view.findViewById(R.id.text_item_at_nombre)
            name.text = nombre
        }

        fun setApellido(apellido: String) {
            val lastName: TextView = view.findViewById(R.id.text_item_at_2)
            lastName.text = apellido
        }

        fun getCardLayout(): CardView {
            // Me posiciono sobre el click especifico de la card
            return view.findViewById(R.id.card_package_item)
        }

    }

}
