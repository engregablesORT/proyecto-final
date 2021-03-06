package com.example.hrit_app.adapters

import com.example.hrit_app.entities.User
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import org.w3c.dom.Text

class AsesorTecnicoListAdapter(
    private var asesores: MutableList<User>,
    val onItemClick: (Int) -> Boolean
) : RecyclerView.Adapter<AsesorTecnicoListAdapter.AsesorTecnicoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsesorTecnicoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_at, parent, false)
        return (AsesorTecnicoListAdapter.AsesorTecnicoHolder(view))
    }

    override fun getItemCount(): Int {
        return asesores.size
    }

    override fun onBindViewHolder(holder: AsesorTecnicoHolder, position: Int) {
        holder.setName(asesores[position].name)
        holder.setApellido(asesores[position].lastName)
        holder.setTitulo(asesores[position].titulo)
        holder.getCardLayout().setOnClickListener() {
            onItemClick(position)
        }
    }

    class AsesorTecnicoHolder(v: View) : RecyclerView.ViewHolder(v) {
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

        fun setTitulo(titulo: String) {
            val tituloVista: TextView = view.findViewById(R.id.text_item_at_3)
            tituloVista.text = titulo
        }

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.card_package_item)
        }

    }

}
