package com.example.hrit_app.fragments.rrhh

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

class FragmentHR_perfil : Fragment() {

    lateinit var v: View
    lateinit var btnEditar: ImageView
    lateinit var btnGuardar: Button

    // Inputs de usuario HR
    lateinit var inputFirstName: TextInputLayout
    lateinit var inputFirstNameEdit: TextInputEditText
    lateinit var inputLastName: TextInputLayout
    lateinit var inputLastNameEdit: TextInputEditText
    lateinit var inputPassword: TextInputLayout
    lateinit var inputPasswordEdit: TextInputEditText
    lateinit var inputEmail: TextInputLayout
    lateinit var inputEmailEdit: TextInputEditText

    // Valores
    lateinit var textoFirstName: String
    lateinit var textoLastName: String
    lateinit var textoEmail: String
    lateinit var textoPassword: String

    // User
    lateinit var user: User
    var userService: UserService = UserService()

    // SharedPreferences
    lateinit var sharedPreferences: SharedPreferences

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
        btnGuardar = v.findViewById(R.id.btnGuardar)

        // Esto deberia ir en el xml... creo
        inputPassword.isEnabled = false
        inputLastName.isEnabled = false
        inputEmail.isEnabled = false
        inputFirstName.isEnabled = false

        // Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )

        return v
    }

    override fun onStart() {
        super.onStart()

        val emailKey = sharedPreferences.getString(SharedPreferencesKey.EMAIL, "").toString()
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            user = userService.findByEmail(emailKey)!!
            setUserInputValues()
        }

        btnEditar.setOnClickListener {
            inputFirstName.isEnabled = true
            inputLastName.isEnabled = true
            inputPassword.isEnabled = true
            inputEmail.isEnabled = true

            btnGuardar.visibility = View.VISIBLE

            inputFirstNameEdit.setText(textoFirstName.toString())
            inputLastNameEdit.setText(textoLastName.toString())
            inputPasswordEdit.setText(textoPassword.toString())
            inputEmailEdit.setText(textoEmail.toString())

            inputFirstName.hint = "Nombre"
            inputLastName.hint = "Apellido"
            inputPassword.hint = "Password"
            inputEmail.hint = "Email"
        }

        btnGuardar.setOnClickListener {
            var userNuevo = User(
                inputEmailEdit.text.toString(),
                inputPasswordEdit.text.toString(),
                inputFirstNameEdit.text.toString(),
                inputLastNameEdit.text.toString(),
                user.rol,
                user.tecnologias
            )
            Snackbar.make(v, "El usuario ha sido actualizado", Snackbar.LENGTH_SHORT).show()
            // userService.deleteUser(user)
            // userService.createUser(userNuevo)
        }
    }

    private fun setUserInputValues() {
        textoFirstName = user.name
        textoLastName = user.lastName
        textoEmail = user.email
        textoPassword = user.password
        inputFirstName.hint = textoFirstName
        inputLastName.hint = textoLastName
        inputPassword.hint = "**********"
        inputEmail.hint = textoEmail
    }

}