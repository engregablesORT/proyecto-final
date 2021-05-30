package com.example.hrit_app.fragments.dev

import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import com.example.hrit_app.services.TecnologiaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.Rol
import com.example.hrit_app.utils.constants.Seniority
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
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var btnGuardarDevPerfil: Button
    lateinit var spinner: Spinner
    lateinit var senioritySeleccionado: String
    // User
    lateinit var user: User
    var tecnologiaService: TecnologiaService = TecnologiaService()
    var userService: UserService = UserService()

    val mapSeniorityPosicion: Map<String, Int> = mapOf( Pair(Seniority.JR, 1),
        Pair(Seniority.SSR, 2),
        Pair(Seniority.SR, 3),
        Pair(Seniority.TL, 4)
    )


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
        emailEditText = v.findViewById(R.id.emailDevEditText)
        passwordEditText = v.findViewById(R.id.passwordDevEditText)
        btnGuardarDevPerfil = v.findViewById(R.id.btnGuardarDevPerfil)
        spinner = v.findViewById(R.id.spinnerSeniority)
        return v
    }

    override fun onStart() {
        super.onStart()
        v.visibility = View.INVISIBLE
        val sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )

        val uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

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
            val userNuevo = User(
                uidKey,
                emailEditText.text.toString(),
                passwordEditText.text.toString(),
                nombreEditText.text.toString(),
                apellidoEditText.text.toString(),
                user.rol,
                user.tecnologias,
                descripcionEditText.text.toString(),
                precioHoraEdit.text.toString(),
                titleEditText.text.toString(),
                senioritySeleccionado,
                ""
            )
            Snackbar.make(v, "El usuario ha sido actualizado", Snackbar.LENGTH_SHORT).show()
            userService.updateAsesorTecnico(userNuevo, uidKey)
            Snackbar.make(v, "Usuario ha sido actualizado", Snackbar.LENGTH_SHORT).show()
        }

        var seniorities = arrayOf("Seleccionar...", Seniority.JR, Seniority.SSR, Seniority.SR, Seniority.TL)
        spinner.adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            seniorities
        )

        spinner.setSelection(0)

        spinner.setBackgroundColor(Color.GREEN)

        spinner.onItemSelectedListener = object :

            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                senioritySeleccionado = seniorities[position]
            }
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
        descripcionEditText.setText(user.descripcion)
        precioHoraEdit.setText(user.precio)
        titleEditText.setText(user.titulo)
        emailEditText.setText(user.email)
        passwordEditText.setText(user.password)
        val posicionSeleccionada = mapSeniorityPosicion?.get(user.seniority)
        if (posicionSeleccionada != null) {
            spinner.setSelection(posicionSeleccionada)
        } else {
            spinner.setSelection(0)
        }
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