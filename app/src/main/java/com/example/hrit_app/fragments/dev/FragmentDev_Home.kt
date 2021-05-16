package com.example.hrit_app.fragments.dev

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        entrevistasPendientes.add(Entrevista( 3, "Martin Guzman" , "web.com", 6 , "10 de Abril - 18:00hs ", 2,5, Entrevista.Constants.estadoPendienteRespuesta))
        entrevistasPendientes.add(Entrevista( 3, "Juan Perez" , "web.com", 6 , "10 de Abril - 18:00hs ", 2,5, Entrevista.Constants.estadoRechazada))

        entrevistasPendientes = findAllPendientes()

        recEntrevistasPendientes.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recEntrevistasPendientes.layoutManager = linearLayoutManager

        entrevistaListAdapter = EntrevistaListAdapter(entrevistasPendientes, { x ->
            onItemClickA(x, "Entrevista aceptada")
        }, {
            x -> onItemClickR(x,"Entrevista rechazada")
        })


      //    entrevistaListAdapter = EntrevistaListAdapter(entrevistasPendientes )

        recEntrevistasPendientes.adapter = entrevistaListAdapter

    }

    fun onItemClickA (x : Int,  text : String ) : Boolean {
        entrevistasPendientes[x].estado = Entrevista.Constants.estadoAceptado
        Snackbar.make(v,text,Snackbar.LENGTH_SHORT).show()
        entrevistasPendientes = findAllPendientes()
        actualizarEntrevistas(entrevistasPendientes)
        return true
    }

    fun onItemClickR (x : Int, text : String ) : Boolean {
        entrevistasPendientes[x].estado = Entrevista.Constants.estadoRechazada
        Snackbar.make(v,text,Snackbar.LENGTH_SHORT).show()
        entrevistasPendientes = findAllPendientes()
        actualizarEntrevistas(entrevistasPendientes)
        return true
    }

    fun findAllPendientes(): MutableList<Entrevista>{
        val entrevistasPendientes = entrevistasPendientes.filter { entrevista -> entrevista.estado.equals(Entrevista.Constants.estadoPendienteRespuesta) }
        return entrevistasPendientes.toMutableList()
    }

    private fun actualizarEntrevistas(entrevistasPendientes: MutableList<Entrevista>){
        entrevistaListAdapter = EntrevistaListAdapter(entrevistasPendientes, { x ->
            onItemClickA(x, "Entrevista aceptada")
        }, {
            x -> onItemClickR(x,"Entrevista rechazada")
        })
        recEntrevistasPendientes.adapter = entrevistaListAdapter
    }





}