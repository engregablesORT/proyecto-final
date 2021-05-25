package com.example.hrit_app.fragments.rrhh

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.AsesorTecnicoListAdapter
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.TecnologiaService
import com.example.hrit_app.services.UserService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class FragmentHR_Home : Fragment() {

    lateinit var v: View
    lateinit var recTecnologias: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var tecnologiaListAdapter: TecnologiaListAdapter
    lateinit var searchView: SearchView
    var tecnologias: MutableList<Tecnologia> = ArrayList<Tecnologia>()
    var tecnologiaService: TecnologiaService = TecnologiaService()

    lateinit var recAsesores: RecyclerView
    lateinit var linearLayoutManagerAsesores: LinearLayoutManager
    lateinit var asesoresListAdapter: AsesorTecnicoListAdapter
    var asesoresTecnicos: MutableList<User> = mutableListOf()
    var userService: UserService = UserService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr__home, container, false)
        // Inflate the layout for this fragment
        recTecnologias = v.findViewById(R.id.recTecnologias)
        recAsesores = v.findViewById(R.id.recAsesoresTecnicos)
        searchView = v.findViewById(R.id.searchView)
        return v
    }


    override fun onStart() {
        super.onStart()

        recAsesores.setHasFixedSize(true)
        linearLayoutManagerAsesores = LinearLayoutManager(context)
        recAsesores.layoutManager = linearLayoutManagerAsesores
        asesoresListAdapter =
            AsesorTecnicoListAdapter(asesoresTecnicos) { x -> onAsesorClick(x) }
        recAsesores.adapter = asesoresListAdapter

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            asesoresTecnicos = userService.findAllAsesoresTecnicos()
            activity?.runOnUiThread {
                actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos)
            }

        }

        /*
        tecnologias = tecnologiaService.getAllTecnologias()
        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologias, {x -> onTecnologiaClick(x)})
        recTecnologias.layoutManager = linearLayoutManager
        recTecnologias.adapter = tecnologiaListAdapter
        */

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //asesoresListAdapter.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                asesoresTecnicos = Collections.EMPTY_LIST as MutableList<User>
                if (newText.replace(" ", "").length > 0) {
                    //asesoresTecnicos = userService.findByNombre(newText)
                } else {
                    //asesoresTecnicos = userService.findAllAsesoresTecnicos()
                }
                actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos)
                return false
            }
        })

    }


    private fun actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos: MutableList<User>) {
        asesoresListAdapter = AsesorTecnicoListAdapter(asesoresTecnicos, { x -> onAsesorClick(x) })
        recAsesores.adapter = asesoresListAdapter
    }

    fun onTecnologiaClick(position: Int): Boolean {
        /**
         * TODO filtrar asesores tecnicos por tecnologia
         *
         * */
        return true
    }

    fun onAsesorClick(position: Int): Boolean {
        /**
         * TODO Crear contratacion fragment
         *
         * */
        return true
    }


}