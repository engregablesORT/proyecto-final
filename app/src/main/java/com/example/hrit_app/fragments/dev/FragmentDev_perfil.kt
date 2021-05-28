package com.example.hrit_app.fragments.dev

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.repository.TecnologiaRepository
import com.example.hrit_app.services.TecnologiaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class FragmentDev_perfil : Fragment() {

    lateinit var v: View
    lateinit var recTecnologias: RecyclerView
    var tecnologias: MutableList<Tecnologia> = ArrayList<Tecnologia>()
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var tecnologiaListAdapter: TecnologiaListAdapter
    lateinit var nombreEditText: EditText
    lateinit var apellidoEditText: EditText
    lateinit var titleEditText: EditText
    lateinit var descripcionEditText: EditText
    lateinit var precioHoraEdit: EditText
    lateinit var experienciaEditText: EditText
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var btnEditarDev: ImageView
    lateinit var btnGuardarDevPerfil: Button

    // User
    lateinit var user: User
    var tecnologiaService: TecnologiaService = TecnologiaService()
    var userService: UserService = UserService()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_dev_perfil, container, false)
        // Inflate the layout for this fragment
        recTecnologias = v.findViewById(R.id.recTecnologias)
        nombreEditText = v.findViewById(R.id.nombreDev)
        apellidoEditText = v.findViewById(R.id.apellidoDev)
        titleEditText = v.findViewById(R.id.tituloDev)
        descripcionEditText = v.findViewById(R.id.descripcionDev)
        precioHoraEdit = v.findViewById(R.id.precioPorHoraEditText)
        experienciaEditText = v.findViewById(R.id.expEditText)
        emailEditText = v.findViewById(R.id.emailDevEditText)
        passwordEditText = v.findViewById(R.id.passwordDevEditText)
        btnEditarDev = v.findViewById(R.id.btnEditarDev)
        btnGuardarDevPerfil = v.findViewById(R.id.btnGuardarDevPerfil)
        return v
    }

    override fun onStart() {
        super.onStart()
        v.visibility = View.INVISIBLE
        val sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )
        // creamos recycler tecnologia
        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Usamos asincronismo
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            user = userService.findByEmail(
                sharedPreferences.getString(SharedPreferencesKey.EMAIL, "").toString()
            )!!
            tecnologias = tecnologiaService.getAllTecnologias()
            var userTecnologias = buscarTecnologias(tecnologias, user.tecnologias)

            activity?.runOnUiThread {
                tecnologiaListAdapter = TecnologiaListAdapter(
                    userTecnologias
                ) { x -> onTecnologiaClick(x) }
                recTecnologias.layoutManager = linearLayoutManager
                recTecnologias.adapter = tecnologiaListAdapter
                setInitialValues(user)

                //activarTecnologias(user.tecnologias, tecnologias)
                v.visibility = View.VISIBLE
            }
        }

        btnGuardarDevPerfil.setOnClickListener {
            // TODO agregar validaciones ---------
            // TODO VER COMO FUNCIONA EL UPDATE
            Snackbar.make(v, "Usuario ha sido actualizado", Snackbar.LENGTH_SHORT).show()
        }

        passwordEditText.setOnClickListener {
            passwordEditText.setText(user.password)
        }

    }


    private fun activarTecnologias(
        tecnologiasDelUsuario: List<Tecnologia>,
        tecnologias: MutableList<Tecnologia>
    ) {
        for (tecUsu in tecnologiasDelUsuario) {
            val tecFiltradas =
                tecnologias.filter { tecnologia -> tecnologia.text.equals(tecUsu.text) }
            val tecFiltrada = tecFiltradas.get(0)
            if (tecFiltrada != null) {
                tecFiltrada.active = true
            }
        }
    }

    private fun setInitialValues(user: User) {
        nombreEditText.setText(user.name)
        apellidoEditText.setText(user.lastName)
        descripcionEditText.setText("Programador JS Senior con mas de 10 años de experiencia. Actualmente me caracterizo por trabajar con React Js ")
        precioHoraEdit.setText("$100,00")
        experienciaEditText.setText("10 años")
        titleEditText.setText("Java SR")
        emailEditText.setText(user.email)
        passwordEditText.setText("*********")
    }

    fun onTecnologiaClick(position: Int): Boolean {
        // Actualizo estado
        val tecnologiaEnCuestion = tecnologias.get(position)
        val isTecActiva = !tecnologiaEnCuestion.active
        tecnologiaEnCuestion.active = isTecActiva
        // Lo reflejo en la lista
        val action: String = if (isTecActiva) "agregado." else "removido."
        Snackbar.make(v, tecnologiaEnCuestion.text + " fue " + action, Snackbar.LENGTH_SHORT).show()
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologias, { x -> onTecnologiaClick(x) })
        recTecnologias.adapter = tecnologiaListAdapter
        return true
    }

    fun buscarTecnologias(
        tecnologias: MutableList<Tecnologia>,
        nombreTecnologias: List<String>
    ): MutableList<Tecnologia> {
        var userTecnologias = mutableListOf<Tecnologia>()
        for (tecnologia in tecnologias) {
            for (nombre in nombreTecnologias) {
                if (tecnologia.text.trim() == nombre.trim()) {
                    userTecnologias.add(tecnologia)
                }
            }
        }

        return userTecnologias
    }

}