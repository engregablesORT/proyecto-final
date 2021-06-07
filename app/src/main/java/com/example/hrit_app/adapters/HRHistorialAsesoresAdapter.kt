package com.example.hrit_app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.entities.User

class HRHistorialAsesoresAdapter(
    private var asesoresList: MutableList<User>,
    val onClick: (Int) -> Boolean
) : RecyclerView.Adapter<HRHistorialAsesoresAdapter.HRHistorialAsesoresAdapterHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HRHistorialAsesoresAdapterHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historialhr_asesor, parent, false)



        return (HRHistorialAsesoresAdapterHolder(v))
    }

    override fun onBindViewHolder(holder: HRHistorialAsesoresAdapterHolder, position: Int) {
        holder.setNombre(asesoresList[position].name, asesoresList[position].lastName)
        holder.setTitular(asesoresList[position].titulo)
        holder.setValoracion(asesoresList[position].valoracion)
        holder.getCardLayout().setOnClickListener{
            onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return asesoresList.size
    }

    // Holder
    class HRHistorialAsesoresAdapterHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        @SuppressLint("SetTextI18n")
        fun setNombre(nombre: String, apellido: String) {
            view.findViewById<TextView>(R.id.historialhr_nombre).text = "$nombre $apellido"
        }

        fun setTitular(titular: String) {
            view.findViewById<TextView>(R.id.historialhr_titular).text = titular
        }

        fun setValoracion(valoracion: Number) {
            view.findViewById<TextView>(R.id.historialhr_valoracion).text = valoracion.toString()
            view.findViewById<RatingBar>(R.id.ratingBar).rating = valoracion.toFloat()
        }

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.card_package_item)
        }

    }
}
