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

    val mapImgById: Map<Int, Int> = mapOf(
        Pair(700005, R.drawable.java),
        Pair(700093, R.drawable.nodejs),
        Pair(700136, R.drawable.python),
        Pair(700123, R.drawable.react),
        Pair(700167, R.drawable.php),
        Pair(700150, R.drawable.js),
        Pair(700140, R.drawable.angular)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TecnologiaHolder {
        // Aca es donde referencio los items hijos del parent
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tecnologia, parent, false)
        return (TecnologiaListAdapter.TecnologiaHolder(view))
    }

    override fun getItemCount(): Int {
        return tecnologias.size
    }

    override fun onBindViewHolder(holder: TecnologiaHolder, position: Int) {
        // Necesitamos hacer esto para obtener el id exacto del la imagen en /drawable
        //holder.setText(tecnologias[position].text)
        val idDrawable: Int? = mapImgById?.get(tecnologias[position].imgsrc)
        if (idDrawable != null) {
            holder.setImg(idDrawable)
        }
        if (tecnologias[position].active) {
            holder.itemView.tecnologiaActivaDev.visibility = View.VISIBLE
        }

        holder.getCardLayout().setOnClickListener(){
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
