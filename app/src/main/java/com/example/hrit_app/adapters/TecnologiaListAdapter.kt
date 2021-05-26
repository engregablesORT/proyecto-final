package com.example.hrit_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.entities.Tecnologia
import kotlinx.android.synthetic.main.item_tecnologia.view.*

class TecnologiaListAdapter(
        private var tecnologias: MutableList<Tecnologia>,
        val onItemClick: (Int) -> Boolean
) : RecyclerView.Adapter<TecnologiaListAdapter.TecnologiaHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TecnologiaHolder {
        // Aca es donde referencio los items hijos del parent
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tecnologia, parent, false)
        return (TecnologiaListAdapter.TecnologiaHolder(view))
    }

    override fun getItemCount(): Int {
        return tecnologias.size
    }

    override fun onBindViewHolder(holder: TecnologiaHolder, position: Int) {
        // Aca mi informacion que seria el nombre de la pelicula, impacta en el holder y lo muestro
        holder.setText(tecnologias[position].text)
        holder.setImg(tecnologias[position].imgsrc)
        if (tecnologias[position].active) {
            holder.itemView.tecnologiaActivaDev.visibility = View.VISIBLE
        }

        holder.getCardLayout().setOnLongClickListener(){
            onItemClick(position)
        }
    }

    class TecnologiaHolder(v: View) : RecyclerView.ViewHolder(v) {
        // Contiene acciones y referencias con respecto a la vista
        // Conexion entre adapter y xml del item
        private var view: View

        init {
            this.view = v
        }

        fun setText(text: String){
            val txt: TextView = view.findViewById(R.id.text_item_disponibilidad)
            txt.text = text
        }

        fun setImg(id: Int){
            val img: ImageView = view.findViewById(R.id.item_img_tec)
            img.setImageResource(id)
        }

        fun getCardLayout (): CardView {
            // Me posiciono sobre el click especifico de la card
            return view.findViewById(R.id.card_package_item_AT)
        }

    }

}
