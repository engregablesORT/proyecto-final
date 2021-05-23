package com.example.hrit_app.fragments.login

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import com.example.hrit_app.R
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.Rol
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_signup.*
import java.util.*

class Fragment_signup : Fragment() {
    lateinit var v: View
    lateinit var btnRegistrarConfirmar: Button
    lateinit var name: EditText
    lateinit var lastName: EditText
    lateinit var userName: EditText
    lateinit var passWord: EditText
    lateinit var rePassWord: EditText
    lateinit var spinner: Spinner
    lateinit var rolSeleccionado: String
    var userService: UserService = UserService()
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_signup, container, false)
        userName = v.findViewById(R.id.email_signin)
        passWord = v.findViewById(R.id.password_signin)
        btnRegistrarConfirmar = v.findViewById(R.id.btn_signup_2)
        lastName = v.findViewById(R.id.lastName)
        name = v.findViewById(R.id.name)
        rePassWord = v.findViewById(R.id.rePassword)
        spinner = v.findViewById(R.id.spinner)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        // Initialize Firebase Auth
        btnRegistrarConfirmar.setOnClickListener {
            if (verificarDatosObligatorios()) {
                if (verificarPasswordIguales()) {
                    crearNuevoUsuario(
                        userName.text.toString(),
                         passWord.text.toString()
                    )
                    Snackbar.make(v,"El usuario ha sido creado", Snackbar.LENGTH_SHORT).show()
                    redirectToAction1()
                } else {
                    Snackbar.make(v,"Las password ingresadas deben ser iguales", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(v,"Faltan completar algunos datos", Snackbar.LENGTH_SHORT).show()
            }
        }

        var roles = arrayOf("Seleccionar un Rol ...", Rol.RH, Rol.AT)
        spinner.adapter = ArrayAdapter<String>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, roles)

        spinner.setSelection(0)

        spinner.onItemSelectedListener = object :

                AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                rolSeleccionado = roles[position]
            }
        }
    }

    fun verificarDatosObligatorios(): Boolean {
        return userName.text.length > 0  && passWord.text.length > 0 &&
                rePassWord.text.length > 0 && name.text.length > 0 &&
                lastName .text.length > 0 &&
                (spinner.selectedItem.equals(Rol.RH) || spinner.selectedItem.equals(Rol.AT))
    }

    fun verificarPasswordIguales(): Boolean {
        return passWord.text.toString().equals(rePassWord.text.toString())
    }

    private fun redirectToAction1(){
        val action1 = Fragment_signupDirections.actionFragmentSignupToFragmentLogin()
        v.findNavController().navigate(action1)
    }

    private fun crearNuevoUsuario(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val uid = user.uid
                    System.out.println(userName.text.toString() +  " " +  passWord.text.toString() +  " " + name.text.toString() +  " " + lastName.text.toString() +  " " + spinner.selectedItem.toString() + " " + uid)

                    //       updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
             //       Toast.makeText( "Authentication failed.",
                //        Toast.LENGTH_SHORT).show()
        //            updateUI(null)
                }
            }

     // val user = User(userName.text.toString(), passWord.text.toString(), name.text.toString(), lastName.text.toString(), spinner.selectedItem.toString(), uid)
     //   userService.createUser(user)
    }
}