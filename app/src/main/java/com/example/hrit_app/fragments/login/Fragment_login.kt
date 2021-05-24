package com.example.hrit_app.fragments.login

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.hrit_app.R
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.Rol
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class Fragment_login : Fragment() {

    lateinit var v: View
    lateinit var btnLogin: Button
    lateinit var btnRegistrar: Button
    lateinit var userName: EditText
    lateinit var passWord: EditText
    lateinit var welcomeMessage: TextView
    var userService: UserService = UserService()
    private lateinit var auth: FirebaseAuth

    // Shared Preferences name const

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_login, container, false)
        btnLogin = v.findViewById(R.id.btn_login)
        btnRegistrar = v.findViewById(R.id.btn_signup)
        userName = v.findViewById(R.id.userName)
        passWord = v.findViewById(R.id.passWord)
        welcomeMessage = v.findViewById(R.id.welcomeMessage)
        auth = Firebase.auth
        return v
    }

    override fun onStart() {
        super.onStart()
        welcomeMessage.setText("Bienvenido a < HR&IT />")

        val sharedPreferences = requireContext().getSharedPreferences(SharedPreferencesKey.PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val currentUser = auth.currentUser
        if(currentUser != null) {
            //update
        }
        btnLogin.setOnClickListener {
            auth.signInWithEmailAndPassword(userName.text.toString(), passWord.text.toString()).addOnCompleteListener(requireActivity()){task ->
                if (task.isSuccessful){
                    val parentJob = Job()
                    val scope = CoroutineScope(Dispatchers.Default + parentJob)
                    scope.launch {
                        val user =  verificarSiElUsuarioExiste(userName.text.toString())
                        if (user != null) {
                            editor.putString(SharedPreferencesKey.EMAIL, userName.text.toString())
                            editor.apply()
                            redirectToDevActivityOrHrActivity(user)
                        }
                    }
                }
            }
        }
        btnRegistrar.setOnClickListener {
            redirectToRegisterPage()
        }
    }

    private suspend fun verificarSiElUsuarioExiste(email: String): User? {
        try {
            val user = userService.findRolByEmail(email)
            return user
        } catch (e: Resources.NotFoundException) {
            Snackbar.make(v, "Credenciales Incorrectas", Snackbar.LENGTH_SHORT).show()
        }
        return null
    }

    private fun redirectToRegisterPage() {
        val action2 = Fragment_loginDirections.actionFragmentLoginToFragmentSignup()
        v.findNavController().navigate(action2)
    }

    private fun redirectToDevActivityOrHrActivity(user: User) {
        if (Rol.AT.equals(user.rol)) {
            redirectToDevActivity()
        } else {
            redirectToHrActivity()
        }
    }

    private fun redirectToDevActivity() {
        val appActivityAction = Fragment_loginDirections.actionFragmentLoginToActivityDev2()
        v.findNavController().navigate(appActivityAction)
    }

    private fun redirectToHrActivity() {
        val appActivityAction = Fragment_loginDirections.actionFragmentLoginToActivityHR()
        v.findNavController().navigate(appActivityAction)
    }
}