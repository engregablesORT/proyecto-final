package com.example.hrit_app.fragments.rrhh

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class FragmentHR_Home : Fragment() {

    lateinit var v: View
    lateinit var recTecnologias: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var tecnologiaListAdapter: TecnologiaListAdapter
    lateinit var filterImage : ImageFilterView
    var tecnologias : MutableList<Tecnologia> = ArrayList<Tecnologia>()
    var tecnologiaService: TecnologiaService = TecnologiaService()

    lateinit var recAsesores: RecyclerView
    lateinit var linearLayoutManagerAsesores: LinearLayoutManager
    lateinit var asesoresListAdapter: AsesorTecnicoListAdapter
    var asesoresTecnicos : MutableList<User> = ArrayList<User>()
    var userService: UserService = UserService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_hr__home, container, false)
        // Inflate the layout for this fragment
        recTecnologias = v.findViewById(R.id.recTecnologias)
        recAsesores = v.findViewById(R.id.recAsesoresTecnicos)
        filterImage = v.findViewById(R.id.filterImage)
        return v
    }


    override fun onStart() {
        super.onStart()
        tecnologias = tecnologiaService.getAllTecnologias()
        asesoresTecnicos = userService.findAllAsesoresTecnicos()

        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologias, {x -> onTecnologiaClick(x)})
        recTecnologias.layoutManager = linearLayoutManager
        recTecnologias.adapter = tecnologiaListAdapter

        // Recycler Vertical
        linearLayoutManagerAsesores = LinearLayoutManager(context)
        asesoresListAdapter = AsesorTecnicoListAdapter(asesoresTecnicos, {x -> onAsesorClick(x)} )
        recAsesores.layoutManager = linearLayoutManagerAsesores
        recAsesores.adapter = asesoresListAdapter

        filterImage.setOnClickListener(){
            /**
             * TODO redirigir a nuevo fragment para filtros.
             * ***/
        }

    }

    fun onTecnologiaClick (position: Int): Boolean {
        /**
         * TODO filtrar asesores tecnicos por tecnologia
         *
         * */
        return true
    }

    fun onAsesorClick (position: Int): Boolean {
        /**
         * TODO Crear contratacion
         *
         * */
        return true
    }



}