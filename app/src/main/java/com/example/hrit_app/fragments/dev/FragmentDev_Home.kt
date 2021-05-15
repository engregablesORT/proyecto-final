package com.example.hrit_app.fragments.dev

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.EntrevistaListAdapter
import com.example.hrit_app.entities.Entrevista
import com.google.android.material.snackbar.Snackbar

class FragmentDev_Home : Fragment() {

    lateinit var v: View

    lateinit var recEntrevistasPendientes : RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager


    var entrevistasPendientes : MutableList<Entrevista> = ArrayList<Entrevista>()
    private lateinit var entrevistaListAdapter: EntrevistaListAdapter

    companion object {
        fun newInstance() = FragmentDev_Home()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v =  inflater.inflate(R.layout.fragment_dev__home, container, false)
        recEntrevistasPendientes = v.findViewById(R.id.rec_entrevistasPendientes)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        entrevistasPendientes.add(Entrevista( 1, "Valentina Gonzalez" , "Snoop Consulting", 2 , "10 de Abril - 18:00hs ", 2,5, Entrevista.Constants.estadoPendienteRespuesta))
        entrevistasPendientes.add(Entrevista( 2, "Victoria Lopez" , "Accenture", 3 , "10 de Abril - 18:00hs ", 2,5, Entrevista.Constants.estadoPendienteRespuesta))
        entrevistasPendientes.add(Entrevista( 3, "Valentina Gonzalez" , "web.com", 6 , "10 de Abril - 18:00hs ", 2,5, Entrevista.Constants.estadoPendienteRespuesta))


        recEntrevistasPendientes.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recEntrevistasPendientes.layoutManager = linearLayoutManager

        entrevistaListAdapter = EntrevistaListAdapter(entrevistasPendientes) { x ->
            onItemClick(x)
        }

      //    entrevistaListAdapter = EntrevistaListAdapter(entrevistasPendientes )

        recEntrevistasPendientes.adapter = entrevistaListAdapter

    }

    fun onItemClick ( position : Int ) : Boolean {
        Snackbar.make(v,position.toString(),Snackbar.LENGTH_SHORT).show()
        return true
    }



}