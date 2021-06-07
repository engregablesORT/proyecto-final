package com.example.hrit_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.entities.Entrevista


class TransaccionListAdapter(private var transacciones: MutableList<Entrevista>) : RecyclerView.Adapter<TransaccionListAdapter.TransaccionHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransaccionListAdapter.TransaccionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaccion, parent, false)
        return (TransaccionListAdapter.TransaccionHolder(view))
    }

    override fun getItemCount(): Int {
        return transacciones.size
    }

    override fun onBindViewHolder(holder: TransaccionHolder, position: Int) {
        holder.setNombreEmpresaHr(transacciones[position].nombreEmpresaHr)
        holder.setFecha(transacciones[position].fecha)
        holder.setPrecio(transacciones[position].precio)
    }

    class TransaccionHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View

        init {
            this.view = v
        }

        fun setNombreEmpresaHr(nombre: String) {
            val name: TextView = view.findViewById(R.id.textItemNombreEmpresa)
            name.text = nombre
        }

        fun setFecha(fecha: String) {
            val lastName: TextView = view.findViewById(R.id.textItemFecha)
            lastName.text = fecha.substring(0,10)
        }

        fun setPrecio(precio: Int) {
            val tituloVista: TextView = view.findViewById(R.id.textItemMonto)
            tituloVista.text = precio.toString()
        }

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.card_package_item)
        }

    }



}