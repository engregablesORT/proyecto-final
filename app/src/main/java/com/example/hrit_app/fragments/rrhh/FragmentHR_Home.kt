package com.example.hrit_app.fragments.rrhh

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.AsesorTecnicoListAdapter
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.TecnologiaService
import com.example.hrit_app.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


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

        // creamos recycler asesores
        recAsesores.setHasFixedSize(true)
        linearLayoutManagerAsesores = LinearLayoutManager(context)

        // creamos recycler tecnologia
        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Cargamos los valores tanto para los asesores como para las tecnologias
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            asesoresTecnicos = userService.findAllAsesoresTecnicos()
            tecnologias = tecnologiaService.getAllTecnologias()
            activity?.runOnUiThread {
                actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos)
                setTecnolgias(tecnologias)
            }

        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }


            override fun onQueryTextChange(newText: String): Boolean {
                val parentJob = Job()
                val scope = CoroutineScope(Dispatchers.Default + parentJob)
                scope.launch {
                    if (newText.replace(" ", "").length > 0) {
                        asesoresTecnicos = userService.findByNombre(newText, asesoresTecnicos)
                    } else {
                        asesoresTecnicos = userService.findAllAsesoresTecnicos()
                    }
                    activity?.runOnUiThread {
                        actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos)
                    }
                }
                return false
            }
        })

    }


    private fun actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos: MutableList<User>) {
        asesoresListAdapter = AsesorTecnicoListAdapter(asesoresTecnicos, { x -> onAsesorClick(x) })
        recAsesores.layoutManager = linearLayoutManagerAsesores
        recAsesores.adapter = asesoresListAdapter
    }

    private fun setTecnolgias(tecnologias: MutableList<Tecnologia>) {
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologias, { x -> onTecnologiaClick(x) })
        recTecnologias.layoutManager = linearLayoutManager
        recTecnologias.adapter = tecnologiaListAdapter

    }

    fun onTecnologiaClick(position: Int): Boolean {
        /**
         * TODO filtrar asesores tecnicos por tecnologia
         *
         * */
        return true
    }

    private fun onAsesorClick(position: Int): Boolean {
        val asesor = asesoresTecnicos[position]
        val action = FragmentHR_HomeDirections.actionFragmentHRHomeToFragmentHRContratar(asesor)
        v.findNavController().navigate(action)
        return true
    }


}
