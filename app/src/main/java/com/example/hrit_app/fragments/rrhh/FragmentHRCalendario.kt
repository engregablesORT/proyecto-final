package com.example.hrit_app.fragments.rrhh

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.HREntrevistaEstadoAdapter
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.EntrevistaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.LoadingDialog
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentHRCalendario : Fragment() {

    // private var entrevistasList = entrevistas
    private lateinit var entrevistasList: MutableList<Entrevista>
    private lateinit var entrevistasFiltradas: MutableList<Entrevista>

    // Vista
    private lateinit var v: View

    // Recycler View
    private lateinit var recEntrevistas: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var entrevistaListAdapter: HREntrevistaEstadoAdapter
    private lateinit var recListaVacia: TextView

    // Dialog
    private lateinit var dialogContacto: AlertDialog.Builder
    private lateinit var dialogDescartar: AlertDialog.Builder

    // Services
    private var userService = UserService()
    private var entrevistaService = EntrevistaService()

    private lateinit var chipGroup: ChipGroup
    private lateinit var usersDev: MutableList<User>

    // Dialog Loading
    private lateinit var dialogLoading: LoadingDialog

    // SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    // Asincronismo
    val parentJob = Job()
    val scope = CoroutineScope(Dispatchers.Default + parentJob)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_calendario_estado, container, false)
        recEntrevistas = v.findViewById(R.id.rec_calendariohr_pendientes)
        recListaVacia = v.findViewById(R.id.rec_calendariohr_empty)
        chipGroup = v.findViewById(R.id.calendarhr_chips)

        // Dialog cargando
        dialogLoading = activity?.let { LoadingDialog(it) }!!
        dialogLoading.cargando()

        // Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )

        return v
    }

    override fun onStart() {
        super.onStart()

        val uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

        scope.launch {
            entrevistasList = entrevistaService.findAllEntrevistasByHR(uidKey)
            usersDev = userService.findAllAsesoresTecnicos()
            recEntrevistas.setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(context)
            // setNombresDev()
            activity?.runOnUiThread {
                recEntrevistas.layoutManager = linearLayoutManager
                setRecyclerView2("Aceptadas")
                dialogLoading.terminarCargando()
            }
        }

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            setRecyclerView2(v.findViewById<Chip>(checkedId).text.toString())
        }


    }

    // Funciones para enviar al adapter
    private fun onContactoClick(x: Int): Boolean {
        val entrevista = entrevistasFiltradas[x]
        scope.launch {
            val userDev = userService.findByID(entrevista.idUserDev)
            activity?.runOnUiThread {
                if (userDev != null) {
                    crearDialogContacto(userDev)
                }
                dialogContacto.show()
            }
        }
        return true
    }

    private fun onDescartarClick(x: Int): Boolean {
        val entrevista = entrevistasFiltradas[x]
        scope.launch {
            activity?.runOnUiThread {
                crearDialogDescartar(x, entrevista)
                dialogDescartar.show()
            }
        }
        return true
    }

    private fun onItemClick(x: Int): Boolean {
        return true
    }

    // Dialogs
    private fun crearDialogContacto(userDev: User) {
        val stringDialog =
            "Podras contactarte con ${userDev.name} ${userDev.lastName} al correo electronico:\n- ${userDev.email}"
        dialogContacto = AlertDialog.Builder(this.context);
        dialogContacto.setTitle("Datos de contacto");
        dialogContacto.setMessage(stringDialog);
        dialogContacto.setPositiveButton("Ok") { _, _ ->
        }
    }

    private fun crearDialogDescartar(posicionEntrevista: Int, entrevista: Entrevista) {
        val stringDialog =
            "Desea cancelar la solicitud entrevista?"
        dialogDescartar = AlertDialog.Builder(this.context);
        dialogDescartar.setTitle("Cancelar entrevista");
        dialogDescartar.setMessage(stringDialog);
        dialogDescartar.setNegativeButton("No") { _, _ -> }
        dialogDescartar.setPositiveButton("Si, cancelar")
        { _, _ ->
            entrevistaService.updateEntrevistaEstado(
                entrevista.id,
                Entrevista.Constants.estadoCancelada
            )
            val entrevistaBorrada = entrevistasFiltradas.removeAt(posicionEntrevista)
            entrevistasList.remove(entrevistaBorrada)
            setRecyclerView2("Pendientes")
        }
    }

    // Set de los datos del recycler view
    private fun setRecyclerView() {
        entrevistaListAdapter = HREntrevistaEstadoAdapter(entrevistasList) { x ->
            onItemClick(x)
        }

        if (entrevistasList.isEmpty()) {
            recListaVacia.visibility = View.VISIBLE
            recEntrevistas.visibility = View.INVISIBLE
        } else {
            entrevistaListAdapter =
                if (entrevistasList[0].estado == Entrevista.Constants.estadoPendienteRespuesta) {
                    HREntrevistaEstadoAdapter(entrevistasList) { x ->
                        onDescartarClick(x)
                    }
                } else {
                    HREntrevistaEstadoAdapter(entrevistasList) { x ->
                        onContactoClick(x)
                    }
                }
        }

        recEntrevistas.adapter = entrevistaListAdapter
    }

    private fun setRecyclerView2(estado: String) {
        val estadoConstant = buscarConstantEstado(estado)
        entrevistasFiltradas = filtrarEntrevistasPorEstado(entrevistasList, estadoConstant)

        if (entrevistasFiltradas.isEmpty()) {
            listaVaciaView()
        } else {
            recListaVacia.visibility = View.INVISIBLE
            recEntrevistas.visibility = View.VISIBLE

            entrevistaListAdapter =
                when (entrevistasFiltradas[0].estado) {
                    Entrevista.Constants.estadoAceptado -> {
                        HREntrevistaEstadoAdapter(entrevistasFiltradas) { x ->
                            onContactoClick(x)
                        }
                    }
                    Entrevista.Constants.estadoPendienteRespuesta -> {
                        HREntrevistaEstadoAdapter(entrevistasFiltradas) { x ->
                            onDescartarClick(x)
                        }
                    }

                    else -> HREntrevistaEstadoAdapter(entrevistasFiltradas) { x ->
                        onItemClick(x)
                    }
                }
        }

        recEntrevistas.adapter = entrevistaListAdapter
    }

    // Metodos para poder filtrar la entrevista
    private fun filtrarEntrevistasPorEstado(
        entrevistas: MutableList<Entrevista>,
        estado: String
    ): MutableList<Entrevista> {
        return entrevistas.filter { entrevista -> entrevista.estado == estado } as MutableList<Entrevista>
    }

    private fun buscarConstantEstado(estado: String): String {
        var estadoConstant = ""
        when (estado) {
            "Aceptadas" -> {
                estadoConstant = Entrevista.Constants.estadoAceptado
            }
            "Rechazadas" -> {
                estadoConstant = Entrevista.Constants.estadoRechazada
            }
            "Completadas" -> {
                estadoConstant = Entrevista.Constants.estadoFinalizada
            }
            "Canceladas" -> {
                estadoConstant = Entrevista.Constants.estadoCancelada
            }
            "Pendientes" -> {
                estadoConstant = Entrevista.Constants.estadoPendienteRespuesta
            }
        }
        return estadoConstant
    }

    private fun listaVaciaView() {
        recListaVacia.visibility = View.VISIBLE
        recEntrevistas.visibility = View.INVISIBLE
    }

    private fun setNombresDev() {
        for (entrevista in entrevistasList) {
            val userDev = usersDev.find { it.id == entrevista.idUserDev }
            if (userDev != null) {
                entrevista.idUserDev = "${userDev.name} ${userDev.lastName}"
            }
        }
    }
}
