package com.example.hrit_app.fragments.dev

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.fragments.rrhh.FragmentHRContratarArgs
import com.example.hrit_app.services.TecnologiaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.Seniority
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentDev_EditTecnologias : Fragment() {

    lateinit var v: View
    lateinit var recTecnologias: RecyclerView
    var tecnologiasParaAdapter: MutableList<Tecnologia> = ArrayList<Tecnologia>()
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var tecnologiaListAdapter: TecnologiaListAdapter
    var tecnologiaService: TecnologiaService = TecnologiaService()
    private lateinit var user: User
    lateinit var btnTecnologias: Button
    // SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences
    var userService: UserService = UserService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dev__edit_tecnologias, container, false)
        recTecnologias = v.findViewById(R.id.recTecnologias)
        btnTecnologias = v.findViewById(R.id.btnEditarTecnologias)

        // Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )
        return v
    }

    override fun onStart() {
        super.onStart()
        user = FragmentDev_EditTecnologiasArgs.fromBundle(requireArguments()).user
        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Usamos asincronismo
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            tecnologiasParaAdapter = tecnologiaService.getAllTecnologias()
            activity?.runOnUiThread {
                activarTecnologias(user.tecnologias, tecnologiasParaAdapter)
                activity?.runOnUiThread {
                tecnologiaListAdapter = TecnologiaListAdapter(tecnologiasParaAdapter) { x -> onTecnologiaClick(x) }
                recTecnologias.layoutManager = linearLayoutManager
                recTecnologias.adapter = tecnologiaListAdapter
                }
            }
        }

        btnTecnologias.setOnClickListener {
            val tecnologiasActivasDelUsuario: List<String> = obtenerTecnologiasActivas(tecnologiasParaAdapter)
            val uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()
            userService.updateTecnologiasUser(tecnologiasActivasDelUsuario , uidKey)
        }
    }

    private fun obtenerTecnologiasActivas(tecnologiasParaAdapter: MutableList<Tecnologia>): List<String> {
        return tecnologiasParaAdapter.filter { tecnologia -> tecnologia.active }.map { tecActiva -> tecActiva.text }
    }

    private fun activarTecnologias(tecnologiasDelUsuario: List<String>, tecnologiasParaAdapter: MutableList<Tecnologia>) {
        for (tecnologiaUsuario in tecnologiasDelUsuario) {
            val tecFiltradas = tecnologiasParaAdapter.filter { tecnologia -> tecnologia.text.equals(tecnologiaUsuario) }
            val tecFiltrada = tecFiltradas.get(0)
            if (tecFiltrada != null) {
                tecFiltrada.active = true
            }
        }
    }

    fun onTecnologiaClick(position: Int): Boolean {
        // Actualizo estado
        val tecnologiaEnCuestion = tecnologiasParaAdapter.get(position)
        val isTecActiva = !tecnologiaEnCuestion.active
        tecnologiaEnCuestion.active = isTecActiva
        // Lo reflejo en la lista
        val action: String = if (isTecActiva) "agregado." else "removido."
        Snackbar.make(v, tecnologiaEnCuestion.text + " fue " + action, Snackbar.LENGTH_SHORT).show()
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologiasParaAdapter, { x -> onTecnologiaClick(x) })
        recTecnologias.adapter = tecnologiaListAdapter
        return true
    }

}