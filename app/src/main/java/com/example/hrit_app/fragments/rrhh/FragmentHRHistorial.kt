package com.example.hrit_app.fragments.rrhh

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.HRHistorialAsesoresAdapter
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.EntrevistaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.LoadingDialog
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FragmentHRHistorial : Fragment() {

    // Vista
    private lateinit var v: View

    // XML
    private lateinit var txtEntrevistasAceptadas: TextView
    private lateinit var txtEntrevistasSolicitadas: TextView
    private lateinit var txtEntrevistasRechazadas: TextView
    private lateinit var txtEntrevistasCanceladas: TextView
    private lateinit var txtTecnologiaUno: TextView
    private lateinit var txtTecnologiaDos: TextView
    private lateinit var txtTecnologiaTres: TextView
    private lateinit var barTecnologiaUno: ProgressBar
    private lateinit var barTecnologiaDos: ProgressBar
    private lateinit var barTecnologiaTres: ProgressBar

    // Services
    private var userService = UserService()
    private var entrevistaService = EntrevistaService()

    // Asesores
    private lateinit var usersAsesores: MutableList<User>

    // Entrevistas del User HR
    private lateinit var entrevistasUser: MutableList<Entrevista>

    // Todas las entrevistas para calcular mas solicitadas
    private lateinit var entrevistas: MutableList<Entrevista>
    private var mapaTecnologias: MutableMap<String, Int> = mutableMapOf()
    private var cantidadConsultasTecnologias: Int = 0
    private lateinit var tecnologiaUno: Map.Entry<String, Int>
    private lateinit var tecnologiaDos: Map.Entry<String, Int>
    private lateinit var tecnologiaTres: Map.Entry<String, Int>

    // Asincronismo
    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + parentJob)

    // SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    // Recycler View Asesores
    private lateinit var recAsesores: RecyclerView
    private lateinit var asesoresAdapter: HRHistorialAsesoresAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_historial, container, false)

        // Elementos de la vista
        txtEntrevistasAceptadas = v.findViewById(R.id.historialhr_entrevistas_aceptadas)
        txtEntrevistasSolicitadas = v.findViewById(R.id.historialhr_entrevistas_solicitadas)
        txtEntrevistasRechazadas = v.findViewById(R.id.historialhr_entrevistas_rechazadas)
        txtEntrevistasCanceladas = v.findViewById(R.id.historialhr_entrevistas_canceladas)
        txtTecnologiaUno = v.findViewById(R.id.historialhr_tecnologia_uno)
        txtTecnologiaDos = v.findViewById(R.id.historialhr_tecnologia_dos)
        txtTecnologiaTres = v.findViewById(R.id.historialhr_tecnologia_tres)
        barTecnologiaUno = v.findViewById(R.id.historialhr_tecnologia_uno_bar)
        barTecnologiaDos = v.findViewById(R.id.historialhr_tecnologia_dos_bar)
        barTecnologiaTres = v.findViewById(R.id.historialhr_tecnologia_tres_bar)
        recAsesores = v.findViewById(R.id.historialhr_rec_asesores)

        // Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )

        // Cargando
        activity?.let { LoadingDialog(it).startLoading() }

        return v
    }


    override fun onStart() {
        super.onStart()

        val uid = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

        scope.launch {
            entrevistasUser = entrevistaService.findAllEntrevistasByHR(uid)
            usersAsesores = userService.findAllAsesoresTecnicos()
            entrevistas = entrevistaService.findAllEntrevistas()
            calcularTecnologiasMasSolicitadas()

            activity?.runOnUiThread {
                setTextosEntrevistas()
                setRecyclerAsesores()
                setTecnologias()
            }
        }
    }

    private fun setTextosEntrevistas() {
        txtEntrevistasAceptadas.text =
            filtrarEntrevistasPorEstado(
                entrevistasUser,
                Entrevista.Constants.estadoAceptado
            ).size.toString()
        txtEntrevistasRechazadas.text =
            filtrarEntrevistasPorEstado(
                entrevistasUser,
                Entrevista.Constants.estadoRechazada
            ).size.toString()
        txtEntrevistasCanceladas.text =
            filtrarEntrevistasPorEstado(
                entrevistasUser,
                Entrevista.Constants.estadoCancelada
            ).size.toString()
        txtEntrevistasSolicitadas.text = entrevistasUser.size.toString()
    }

    private fun filtrarEntrevistasPorEstado(
        entrevistas: MutableList<Entrevista>,
        estado: String
    ): MutableList<Entrevista> {
        return entrevistas.filter { entrevista -> entrevista.estado == estado } as MutableList<Entrevista>
    }

    private fun setRecyclerAsesores() {
        recAsesores.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        usersAsesores.sortByDescending { it.valoracion }
        asesoresAdapter = HRHistorialAsesoresAdapter(usersAsesores) { x -> onAsesorClick(x) }
        recAsesores.layoutManager = linearLayoutManager
        recAsesores.adapter = asesoresAdapter
    }

    private fun onAsesorClick(position: Int): Boolean {
        val asesor = usersAsesores[position]
        val action =
            FragmentHRHistorialDirections.actionFragmentHRHistorialToFragmentHRContratar(asesor)
        v.findNavController().navigate(action)
        return true
    }

    private fun calcularTecnologiasMasSolicitadas() {
        for (entrevista in entrevistas) {
            for (tecnologia in entrevista.tecnologias) {
                cantidadConsultasTecnologias += 1
                if (tecnologia !in mapaTecnologias) {
                    mapaTecnologias[tecnologia] = 1
                } else {
                    val valor = mapaTecnologias[tecnologia]?.plus(1)
                    mapaTecnologias[tecnologia] = valor as Int
                }
            }
        }
        mapaTecnologias = mapaTecnologias.toList().sortedByDescending { (_, value) -> value }
            .toMap() as MutableMap<String, Int>

        tecnologiaUno = mapaTecnologias.maxBy { it.value }!!
        mapaTecnologias.remove(tecnologiaUno.key)
        tecnologiaDos = mapaTecnologias.maxBy { it.value }!!
        mapaTecnologias.remove(tecnologiaDos.key)
        tecnologiaTres = mapaTecnologias.maxBy { it.value }!!
    }

    private fun setTecnologias() {
        txtTecnologiaUno.text = tecnologiaUno.key
        txtTecnologiaDos.text = tecnologiaDos.key
        txtTecnologiaTres.text = tecnologiaTres.key
        barTecnologiaUno.progress = tecnologiaUno.value * 100 / cantidadConsultasTecnologias
        barTecnologiaDos.progress = tecnologiaDos.value * 100 / cantidadConsultasTecnologias
        barTecnologiaTres.progress = tecnologiaTres.value * 100 / cantidadConsultasTecnologias
    }
}


