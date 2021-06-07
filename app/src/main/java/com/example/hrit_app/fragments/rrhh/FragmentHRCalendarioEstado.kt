package com.example.hrit_app.fragments.rrhh

import android.app.AlertDialog
import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FragmentHRCalendarioEstado(entrevistas: MutableList<Entrevista>) : Fragment() {

    // Entrevistas recibidas por parametro
    private var entrevistasList = entrevistas

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
        return v
    }

    override fun onStart() {
        super.onStart()

        recEntrevistas.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recEntrevistas.layoutManager = linearLayoutManager
        setRecyclerView()

    }

    private fun onContactoClick(x: Int): Boolean {
        val entrevista = entrevistasList[x]
        scope.launch {
            val userDev = buscarUsuario(entrevista.idUserDev)
            activity?.runOnUiThread {
                crearDialogContacto(userDev)
                dialogContacto.show()
            }
        }
        return true
    }

    private fun onDescartarClick(x: Int): Boolean {
        val entrevista = entrevistasList[x]
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
            entrevistasList.removeAt(posicionEntrevista)
            setRecyclerView()
        }
    }


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

    private suspend fun buscarUsuario(idUser: String): User {
        return userService.findByID(idUser)!!
    }

}
