package com.example.hrit_app.fragments.rrhh

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.hrit_app.R
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.UserService
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class FragmentHR_perfil : Fragment() {

    lateinit var v: View
    lateinit var txtViewFullName: TextView
    lateinit var btnEditar: ImageView
    lateinit var inputPassword: TextInputLayout
    lateinit var inputPassword2: TextInputEditText
    lateinit var inputEmail: TextInputLayout
    lateinit var inputEmail2: TextInputEditText
    lateinit var btnGuardar: Button

    var userService: UserService = UserService()

    // Shared Preferences name const
    val PREF_NAME = "misPreferencias"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_hr_perfil, container, false)
        txtViewFullName = v.findViewById(R.id.txtFullName)
        btnEditar = v.findViewById(R.id.btnEditar)
        inputPassword = v.findViewById(R.id.txtFieldPassword)
        inputPassword2 = v.findViewById(R.id.inputPasswordEdit)
        inputEmail = v.findViewById(R.id.txtFieldEmail)
        inputEmail2 = v.findViewById(R.id.inputEmailEdit)
        btnGuardar = v.findViewById(R.id.btnGuardar)

        // Esto deberia ir en el xml
        inputPassword.isEnabled = false
        inputEmail.isEnabled = false

        return v
    }

    override fun onStart() {
        super.onStart()

        // Shared Preferences
        val sharedPreferences =
                requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var user = userService.findUserByUsername(sharedPreferences.getString("email", "").toString())

        var textoFullName = user.name.toString() + " " + user.lastName.toString()
        var textoEmail = user.email.toString()
        var textoPassword = user.password.toString()

        txtViewFullName.text = textoFullName
        inputPassword.hint = "**********"
        inputEmail.hint = textoEmail

        btnEditar.setOnClickListener {
            inputPassword.isEnabled = true
            inputEmail.isEnabled = true
            btnGuardar.visibility = View.VISIBLE

            inputEmail2.setText(textoEmail.toString())
            inputPassword2.setText(textoPassword.toString())

            txtViewFullName.text = textoFullName

            inputEmail.hint = "Email"
            inputPassword.hint = "Password"
        }

        btnGuardar.setOnClickListener {
            // TODO funcion del servicio que recibe el email viejo y el nuevo usuario
            // eliminar el viejo y guardar el nuevo
        }
    }

}