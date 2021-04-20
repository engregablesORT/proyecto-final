package com.example.hrit_app.fragments.login

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import com.example.hrit_app.R
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.UserService
import com.google.android.material.snackbar.Snackbar

class Fragment_login : Fragment() {

    lateinit var v: View
    lateinit var btnLogin: Button
    lateinit var btnRegistrar: Button
    lateinit var userName: EditText
    lateinit var passWord: EditText
    var userService: UserService = UserService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false)
        btnLogin = v.findViewById(R.id.btn_login)
        btnRegistrar = v.findViewById(R.id.btn_signup)
        userName = v.findViewById(R.id.userName)
        passWord = v.findViewById(R.id.passWord)
        return v
    }

    override fun onStart() {
        super.onStart()
        btnLogin.setOnClickListener {
            val user = verificarSiElUsuarioExiste(userName.text.toString(), passWord.text.toString())
            if (user != null){
                redirectToAppActivity()
            }
        }
        btnRegistrar.setOnClickListener {
            redirectToRegisterPage()
        }
    }

    private fun verificarSiElUsuarioExiste(email: String, password: String): User? {
        try {
            val user = userService.findUserByUsernameAndPassword(email, password)
            return user
        }catch (e: Resources.NotFoundException) {
            Snackbar.make(v,"Credenciales Incorrectas", Snackbar.LENGTH_SHORT).show()
        }
        return null
    }

    private fun redirectToRegisterPage(){
        val action2 = Fragment_loginDirections.actionFragmentLoginToFragmentSignup()
        v.findNavController().navigate(action2)
    }

    private fun redirectToAppActivity(){
        /*val appActivityAction = LoginFragmentDirections.actionLoginFragmentToAppActivity2()
        v.findNavController().navigate(appActivityAction)*/
    }
}