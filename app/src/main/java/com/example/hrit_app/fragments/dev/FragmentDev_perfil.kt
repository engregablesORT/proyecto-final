package com.example.hrit_app.fragments.dev

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.hrit_app.R
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.Seniority
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.example.hrit_app.utils.constants.Validaciones
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FragmentDev_perfil : Fragment() {

    lateinit var v: View
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
    lateinit var editarTecnologias: Button

    // User
    lateinit var user: User
    var userService: UserService = UserService()

    val mapSeniorityPosicion: Map<String, Int> = mapOf(
        Pair(Seniority.JR, 1),
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
        nombreEditText = v.findViewById(R.id.nombreDev)
        apellidoEditText = v.findViewById(R.id.apellidoDev)
        titleEditText = v.findViewById(R.id.tituloDev)
        descripcionEditText = v.findViewById(R.id.descripcionDev)
        precioHoraEdit = v.findViewById(R.id.precioPorHoraEditText)
        emailEditText = v.findViewById(R.id.emailDevEditText)
        passwordEditText = v.findViewById(R.id.passwordDevEditText)
        btnGuardarDevPerfil = v.findViewById(R.id.btnGuardarDevPerfil)
        spinner = v.findViewById(R.id.spinnerSeniority)
        editarTecnologias = v.findViewById(R.id.editarTecnologiasDev)
        return v
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )
        val uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

        // Usamos asincronismo
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            user = userService.findByID(uidKey)!!
            activity?.runOnUiThread {
                setInitialValues(user)
            }
        }
        btnGuardarDevPerfil.setOnClickListener {
            var mensajeError = validarDatos()

            if(mensajeError.equals(""))
            {
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
                    "",
                    0.0
                )
                Snackbar.make(v, "El usuario ha sido actualizado.", Snackbar.LENGTH_SHORT)
                    .show()
                userService.updateAsesorTecnico(userNuevo, uidKey)
            } else {
                Snackbar.make(v, mensajeError, Snackbar.LENGTH_SHORT)
                    .setTextColor(Color.RED).show()
            }
        }

        editarTecnologias.setOnClickListener {
            redireccionarAEditarTecnologiasFragment(user)
        }

        displaySpinner()
    }

    private fun redireccionarAEditarTecnologiasFragment(user: User) {
        val action =
            FragmentDev_perfilDirections.actionFragmentDevPerfilToFragmentDevEditTecnologias(user)
        v.findNavController().navigate(action)

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

    private fun validarDatos() : String
    {
        var mensajeError = ""

        if(!emailEditText.text.toString().contains('@')  || emailEditText.text.toString().length > Validaciones.MAX_CARACT_EMAIL)
            mensajeError = "El email ingresado es inválido."

        if(passwordEditText.text.toString().length > Validaciones.MAX_CARACT_PASSW || passwordEditText.text.toString().length < Validaciones.MIN_CARACT_PASSW)
            mensajeError = "La contraseña no puede contener menos de " + Validaciones.MIN_CARACT_PASSW + " ni más de " + Validaciones.MAX_CARACT_PASSW + " caracteres."

        if(descripcionEditText.text.toString().length > Validaciones.MAX_CARACT_DESC)
            mensajeError = "La descripción no puede contener más de " + Validaciones.MAX_CARACT_DESC + " caracteres."

        if(titleEditText.text.toString().length > Validaciones.MAX_CARACT_TITLE)
            mensajeError = "El título no puede contener más de " + Validaciones.MAX_CARACT_TITLE + " caracteres."

        if(nombreEditText.text.toString().length > Validaciones.MAX_CARACT_NOMBRE || apellidoEditText.text.toString().length > Validaciones.MAX_CARACT_NOMBRE)
            mensajeError = "El nombre o apellido no puede contener más de " + Validaciones.MAX_CARACT_NOMBRE + " caracteres."

        return mensajeError
    }

    private fun displaySpinner() {
        var seniorities =
            arrayOf("Seleccionar...", Seniority.JR, Seniority.SSR, Seniority.SR, Seniority.TL)
        spinner.adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            seniorities
        )

        spinner.setSelection(0)

        spinner.setBackgroundColor(Color.GREEN)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent?.getChildAt(0) != null) {
                    val spinnerTextView = parent.getChildAt(0) as TextView
                    spinnerTextView.setTextColor(Color.WHITE)
                }
                senioritySeleccionado = seniorities[position]
            }
        }
    }

}