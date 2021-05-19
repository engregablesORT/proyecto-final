package com.example.hrit_app.fragments.dev

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
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
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_disponibilidad.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentDev_perfil : Fragment() {

    lateinit var v: View
    lateinit var recTecnologias: RecyclerView
    var tecnologias : MutableList<Tecnologia> = ArrayList<Tecnologia>()
    var tecnologiaService: TecnologiaService = TecnologiaService()
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
    lateinit var btnEditarDev : ImageView
    lateinit var btnDisponibilidadDev : Button
    lateinit var btnGuardarDevPerfil : Button
    lateinit var horaLunes: TextView
    lateinit var horaMartes: TextView
    lateinit var timePickerLunes: ImageView
    lateinit var timePickerMartes: ImageView
    // User
    lateinit var user: User
    var userService: UserService = UserService()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        btnDisponibilidadDev = v.findViewById(R.id.btnDisponibilidadDev)
        btnGuardarDevPerfil = v.findViewById(R.id.btnGuardarDevPerfil)
        return v
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = requireContext().getSharedPreferences(SharedPreferencesKey.PREF_NAME, Context.MODE_PRIVATE)
        // Recycler View
        user = userService.findUserByUsername(sharedPreferences.getString(SharedPreferencesKey.EMAIL, "").toString())
        setInitialValues(user)
        tecnologias = tecnologiaService.getAllTecnologias()
        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        
        // activar tecnologias
        activarTecnologias(user.tecnologias, tecnologias)
        
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologias, {x -> onTecnologiaClick(x)})
        recTecnologias.layoutManager = linearLayoutManager
        recTecnologias.adapter = tecnologiaListAdapter

        btnDisponibilidadDev.setOnClickListener{
            Snackbar.make(v,"Disponibilidad Apretado", Snackbar.LENGTH_SHORT).show()
           val disponibilidadDialog: View = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_disponibilidad, null)
            val dialogBuilder = AlertDialog.Builder(requireContext())
                    .setView(disponibilidadDialog)
            dialogBuilder.show()

            bindeoDelInputTextEImagenes(disponibilidadDialog)

            // Lunes
            onCheckBoxDia(disponibilidadDialog.checkBoxLunes, horaLunes, timePickerLunes)
            onImgDiaClick(disponibilidadDialog.horaLunesImg, horaLunes)

            // Martes
            onCheckBoxDia(disponibilidadDialog.checkBoxMartes, horaMartes, timePickerMartes)
            onImgDiaClick(disponibilidadDialog.horaMartesImg, horaMartes)

            // TODO deberiamos hacer lo mismo para los siguientes dias hasta el viernes inclusive.

            disponibilidadDialog.btnDisponibilidadAdd.setOnClickListener {
                Snackbar.make(v, "Guardado", Snackbar.LENGTH_SHORT).show()
                // TODO deberiamos ocultar dialogo
                btnGuardarDevPerfil.visibility = View.VISIBLE
            }
        }

        btnEditarDev.setOnClickListener {
            btnGuardarDevPerfil.visibility = View.VISIBLE
            setAllFieldsEditables()
        }

        btnGuardarDevPerfil.setOnClickListener {
            Snackbar.make(v, "Usuario ha sido actualizado", Snackbar.LENGTH_SHORT).show()

        }

    }

    private fun bindeoDelInputTextEImagenes(disponibilidadDialog: View) {
        horaLunes = disponibilidadDialog.valorHoraLunes
        horaMartes = disponibilidadDialog.valorHoraMartes
        timePickerLunes = disponibilidadDialog.horaLunesImg
        timePickerMartes = disponibilidadDialog.horaMartesImg
    }

    private fun onImgDiaClick(disponibilidadDialogDia: View, horaDia: TextView) {
        disponibilidadDialogDia.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{timePiker, hour, min ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, min)
                horaDia.text = SimpleDateFormat("HH:mm").format(cal.time)

            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
    }

    private fun onCheckBoxDia(checkBoxDia: CheckBox, valorHoraDia: TextView, timePickerImg: ImageView) {
        checkBoxDia.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                timePickerImg.visibility = View.VISIBLE
                valorHoraDia.visibility = View.VISIBLE
            }else{
                timePickerImg.visibility = View.INVISIBLE
                valorHoraDia.setText("")
                valorHoraDia.visibility = View.INVISIBLE
            }
        }
    }

    private fun setAllFieldsEditables() {
        nombreEditText.setFocusable(true)
        apellidoEditText.setFocusable(true)
        titleEditText.setFocusable(true)
        descripcionEditText.setFocusable(true)
        precioHoraEdit.setFocusable(true)
        experienciaEditText.setFocusable(true)
        emailEditText.setFocusable(true)
        passwordEditText.setFocusable(true)
    }

    private fun activarTecnologias(tecnologiasDelUsuario: List<Tecnologia>, tecnologias: MutableList<Tecnologia>) {
        for (tecUsu in tecnologiasDelUsuario) {
            val tecFiltradas = tecnologias.filter { tecnologia -> tecnologia.text.equals(tecUsu.text) }
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
        passwordEditText.setText("*******")
    }

    fun onTecnologiaClick (position: Int): Boolean {
        // Actualizo estado
        val tecnologiaEnCuestion = tecnologias.get(position)
        val isTecActiva = !tecnologiaEnCuestion.active
        tecnologiaEnCuestion.active = isTecActiva
        // Lo reflejo en la lista
        val action: String = if (isTecActiva) "agregado." else "removido."
        Snackbar.make(v, tecnologiaEnCuestion.text +" fue " + action, Snackbar.LENGTH_SHORT).show()
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologias, {x -> onTecnologiaClick(x)})
        recTecnologias.adapter = tecnologiaListAdapter
        return true
    }

}