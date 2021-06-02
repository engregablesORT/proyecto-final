package com.example.hrit_app.fragments.rrhh

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog.show
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.fragments.dev.FragmentDev_Home.Companion.newInstance
import com.example.hrit_app.services.TecnologiaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.sql.Time
import java.util.*

class FragmentHRContratar : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    var dialog = DialogEntrevistaConfirm()

    // Date Time
    // Hoy
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minutes = 0

    // Entrevista
    private lateinit var dayEntrevista: Number
    private lateinit var monthEntrevista: Number
    private lateinit var yearEntrevista: Number
    private lateinit var hourEntrevista: Number
    private lateinit var minutesEntrevista: Number

    // View y ViewModel
    // private lateinit var viewModel: FragmentHRContratarViewModel
    private lateinit var v: View

    // XML
    private lateinit var txtNombreCompleto: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtTitulo: TextView
    private lateinit var txtPrecio: TextView
    private lateinit var txtSeniority: TextView
    private lateinit var btnContratar: Button
    private lateinit var btnVolver: ImageView

    // RecyclerView
    private lateinit var recTecnologias: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var tecnologiaListAdapter: TecnologiaListAdapter

    // Asesor
    private lateinit var asesor: User

    private var tecnologiaService: TecnologiaService = TecnologiaService()
    private var tecnologias: MutableList<Tecnologia> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_contratar, container, false)

        txtNombreCompleto = v.findViewById(R.id.txtNombreCompleto)
        txtDescripcion = v.findViewById(R.id.txtDescripcion)
        txtPrecio = v.findViewById(R.id.txtPrecio)
        txtTitulo = v.findViewById(R.id.txtTitulo)
        txtSeniority = v.findViewById(R.id.txtSeniority)
        btnContratar = v.findViewById(R.id.btnContratar)
        btnVolver = v.findViewById(R.id.btnVolver)
        recTecnologias = v.findViewById(R.id.recTecnologiasContratar)

        return v
    }

    override fun onStart() {
        super.onStart()
        asesor = FragmentHRContratarArgs.fromBundle(requireArguments()).asesor

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            setRecyclerView()
        }

        setUserData()

        btnVolver.setOnClickListener {
            v.findNavController()
                .navigate(FragmentHRContratarDirections.actionFragmentHRContratarToFragmentHRHome())
        }

        btnContratar.setOnClickListener {
            getDateTimeCalendar()
            context?.let { it1 -> DatePickerDialog(it1, this, year, month, day).show() }

        }
    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minutes = cal.get(Calendar.MINUTE)
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        txtNombreCompleto.text = asesor.name + " " + asesor.lastName
        txtDescripcion.text = asesor.descripcion
        txtPrecio.text = "$" + asesor.precio
        txtTitulo.text = asesor.titulo
        txtSeniority.text = asesor.seniority
    }

    private suspend fun setRecyclerView() {
        tecnologias = tecnologiaService.getAllTecnologias()
        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val userTecnologias = buscarTecnologias(tecnologias, asesor.tecnologias)

        activity?.runOnUiThread {
            tecnologiaListAdapter = TecnologiaListAdapter(userTecnologias) { x -> onItemClick(x) }
            recTecnologias.layoutManager = linearLayoutManager
            recTecnologias.adapter = tecnologiaListAdapter
        }
    }

    private fun buscarTecnologias(
        tecnologias: MutableList<Tecnologia>,
        nombreTecnologias: List<String>
    ): MutableList<Tecnologia> {
        val userTecnologias = mutableListOf<Tecnologia>()
        for (tecnologia in tecnologias) {
            for (nombre in nombreTecnologias) {
                if (tecnologia.text.trim() == nombre.trim()) {
                    userTecnologias.add(tecnologia)
                }
            }
        }

        return userTecnologias
    }

    private fun onItemClick(x: Int): Boolean {
        return true
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dayEntrevista = dayOfMonth
        monthEntrevista = month
        yearEntrevista = year

        getDateTimeCalendar()
        TimePickerDialog(context, this, hour, minutes, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        hourEntrevista = hourOfDay
        minutesEntrevista = minute

        Log.d(
            "TEST",
            dayEntrevista.toString() + monthEntrevista.toString() + hourEntrevista.toString()
        )

        dialog.show(childFragmentManager, DialogEntrevistaConfirm.TAG)
    }

    // Dialog confirmar entrevista
    class DialogEntrevistaConfirm : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar")
                .setMessage("Desea confirmar la siguiente entrevista? \nDatos de la entrevista")
                .setPositiveButton("Confirmar") { _, _ ->

                    val entrevista =
                        Entrevista("", "flor", "accenture", "", "fecha", 0, 0, "pendiente", "")
                    Log.d("TEST", entrevista.nombreEmpresaHr)
                }
                .setNegativeButton("Cancelar") { _, _ -> }
                .create()

        companion object {
            const val TAG = "PurchaseConfirmationDialog"
        }
    }
}