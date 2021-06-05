package com.example.hrit_app.fragments.rrhh

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
import com.example.hrit_app.adapters.HREntrevistaEstadoAdapter
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.UserService
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
    private lateinit var recEntrevistasAceptadas: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var entrevistaListAdapter: HREntrevistaEstadoAdapter

    // Dialog
    private lateinit var dialogContacto: AlertDialog.Builder

    // Services
    private var userService = UserService()

    // Asincronismo
    val parentJob = Job()
    val scope = CoroutineScope(Dispatchers.Default + parentJob)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_calendario_estado, container, false)
        recEntrevistasAceptadas = v.findViewById(R.id.rec_calendariohr_pendientes)
        return v
    }

    override fun onStart() {
        super.onStart()

        recEntrevistasAceptadas.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recEntrevistasAceptadas.layoutManager = linearLayoutManager
        entrevistaListAdapter = HREntrevistaEstadoAdapter(entrevistasList) { x ->
            onContactoClick(x)
        }
        recEntrevistasAceptadas.adapter = entrevistaListAdapter

    }

    private fun onContactoClick(x: Int): Boolean {
        var userDev: User
        val entrevista = entrevistasList[x]
        scope.launch {
            userDev = buscarUsuario(entrevista.idUserDev)
            activity?.runOnUiThread {
                crearDialog(userDev)
                dialogContacto.show()
            }
        }
        return true
    }

    private fun crearDialog(userDev: User) {
        val stringDialog =
            "Podras contactarte con ${userDev.name} ${userDev.lastName} al correo electronico:\n- ${userDev.email}"
        dialogContacto = AlertDialog.Builder(this.context);
        dialogContacto.setTitle("Datos de contacto");
        dialogContacto.setMessage(stringDialog);
        dialogContacto.setPositiveButton("Ok") { _, _ ->
        }
    }

    private suspend fun buscarUsuario(idUser: String): User {
        var user = User()
        user = userService.findByID(idUser)!!
        return user
    }

    // TODO tarjeta de rechazadas: boton de ver comentarios? pendientes: descartar entrevista, color distinto segun categoria
}