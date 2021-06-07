package com.example.hrit_app.fragments.rrhh

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.hrit_app.R
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentHRPerfil : Fragment() {

    lateinit var v: View
    private lateinit var btnEditar: ImageView
    private lateinit var btnGuardar: Button

    // Inputs de usuario HR
    private lateinit var inputFirstName: TextInputLayout
    private lateinit var inputFirstNameEdit: TextInputEditText
    private lateinit var inputLastName: TextInputLayout
    private lateinit var inputLastNameEdit: TextInputEditText
    private lateinit var inputPassword: TextInputLayout
    private lateinit var inputPasswordEdit: TextInputEditText
    private lateinit var inputEmail: TextInputLayout
    private lateinit var inputEmailEdit: TextInputEditText
    private lateinit var inputTitular: TextInputLayout
    private lateinit var inputTitularEdit: TextInputEditText
    private lateinit var inputEmpresa: TextInputLayout
    private lateinit var inputEmpresaEdit: TextInputEditText

    // Valores
    private lateinit var textoFirstName: String
    private lateinit var textoLastName: String
    private lateinit var textoEmail: String
    private lateinit var textoPassword: String
    private lateinit var textoTitular: String
    private lateinit var textoEmpresa: String

    // User
    private lateinit var user: User
    private var userService: UserService = UserService()

    // SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_perfil, container, false)
        btnEditar = v.findViewById(R.id.btnEditar)

        inputFirstName = v.findViewById(R.id.txtFieldFirstName)
        inputFirstNameEdit = v.findViewById(R.id.inputFirstNameEdit)
        inputLastName = v.findViewById(R.id.txtFieldLastName)
        inputLastNameEdit = v.findViewById(R.id.inputLastNameEdit)
        inputPassword = v.findViewById(R.id.txtFieldPassword)
        inputPasswordEdit = v.findViewById(R.id.inputPasswordEdit)
        inputEmail = v.findViewById(R.id.txtFieldEmail)
        inputEmailEdit = v.findViewById(R.id.inputEmailEdit)
        inputTitular = v.findViewById(R.id.txtFieldTitular)
        inputTitularEdit = v.findViewById(R.id.inputTitularEdit)
        inputEmpresa = v.findViewById(R.id.txtFieldEmpresa)
        inputEmpresaEdit = v.findViewById(R.id.inputEmpresaEdit)
        btnGuardar = v.findViewById(R.id.btnGuardar)

        // Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )

        return v
    }

    override fun onStart() {
        super.onStart()

        // Shared Preferences Keys
        val emailKey = sharedPreferences.getString(SharedPreferencesKey.EMAIL, "").toString()
        val uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

        // Asincronismo
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            user = userService.findByEmail(emailKey)!!
            getUserFieldValues()
            activity?.runOnUiThread {
                setUserHintValues()
            }
        }

        btnEditar.setOnClickListener {
            habilitarFormulario()
            setUserInputValues()
        }

        btnGuardar.setOnClickListener {
            val userNuevo = User(
                "",
                inputEmailEdit.text.toString(),
                inputPasswordEdit.text.toString(),
                inputFirstNameEdit.text.toString(),
                inputLastNameEdit.text.toString(),
                user.rol,
                user.tecnologias,
                "",
                "",
                inputTitularEdit.text.toString(),
                "",
                inputEmpresaEdit.text.toString(),
                user.valoracion
            )
            Snackbar.make(v, "El usuario ha sido actualizado", Snackbar.LENGTH_SHORT).show()
            userService.updateUserHR(userNuevo, uidKey)
            deshabilitarFormulario()
        }
    }

    private fun setUserHintValues() {
        inputFirstNameEdit.setText(textoFirstName)
        inputLastNameEdit.setText(textoLastName)
        inputEmailEdit.setText(textoEmail)
        inputEmpresaEdit.setText(textoEmpresa)
        inputTitularEdit.setText(textoTitular)
        inputPasswordEdit.setText("*******")
    }

    private fun getUserFieldValues() {
        textoFirstName = user.name
        textoLastName = user.lastName
        textoEmail = user.email
        textoPassword = user.password
        textoEmpresa = user.empresa
        textoTitular = user.titulo
    }

    private fun setUserInputValues() {
        inputFirstNameEdit.setText(textoFirstName)
        inputLastNameEdit.setText(textoLastName)
        inputPasswordEdit.setText(textoPassword)
        inputEmailEdit.setText(textoEmail)
        inputTitularEdit.setText(textoTitular)
        inputEmpresaEdit.setText(textoEmpresa)
    }

    private fun habilitarFormulario() {
        inputFirstName.isEnabled = true
        inputLastName.isEnabled = true
        inputPassword.isEnabled = true
        inputEmail.isEnabled = true
        inputEmpresa.isEnabled = true
        inputTitular.isEnabled = true
        inputFirstName.hint = "Nombre"
        inputLastName.hint = "Apellido"
        inputPassword.hint = "Password"
        inputEmail.hint = "Email"
        inputEmpresa.hint = "Empresa"
        inputTitular.hint = "Titular"
        btnGuardar.visibility = View.VISIBLE
    }

    private fun deshabilitarFormulario() {
        inputFirstName.isEnabled = false
        inputLastName.isEnabled = false
        inputPassword.isEnabled = false
        inputEmail.isEnabled = false
        inputEmpresa.isEnabled = false
        inputTitular.isEnabled = false
        inputPasswordEdit.setText("*******")
        inputFirstName.hint = "Nombre"
        inputLastName.hint = "Apellido"
        inputPassword.hint = "Password"
        inputEmail.hint = "Email"
        btnGuardar.visibility = View.INVISIBLE
    }
}