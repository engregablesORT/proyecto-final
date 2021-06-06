package com.example.hrit_app.fragments.rrhh

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.EntrevistaService
import com.example.hrit_app.services.TecnologiaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random.Default.nextInt

class FragmentHRContratar : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    // Date Time
    // Hoy
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minutes = 0

    // Entrevista
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    private lateinit var fechaEntrevista: LocalDate
    private lateinit var horaEntrevista: LocalTime
    private lateinit var fechaHoraEntrevista: LocalDateTime
    private lateinit var duracion: Number
    private lateinit var precio: Number
    private lateinit var tecnologiasConsultadas: List<String>

    // View y ViewModel
    // private lateinit var viewModel: FragmentHRContratarViewModel
    private lateinit var v: View

    // XML
    private lateinit var txtNombreCompleto: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtTitulo: TextView
    private lateinit var txtPrecio: Button
    private lateinit var txtSeniority: Button
    private lateinit var btnContratar: Button
    private lateinit var btnVolver: ImageView

    // RecyclerView
    private lateinit var recTecnologias: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var tecnologiaListAdapter: TecnologiaListAdapter

    // Users
    private lateinit var asesor: User
    private lateinit var userHr: User
    private var tecnologias: MutableList<Tecnologia> = mutableListOf()
    private var userTecnologias: MutableList<Tecnologia> = mutableListOf()

    // Services
    private var tecnologiaService = TecnologiaService()
    private var userService = UserService()
    private var entrevistaService = EntrevistaService()

    // Dialogs
    private lateinit var dialogContratar: AlertDialog.Builder
    private lateinit var dialogDuracion: AlertDialog.Builder
    private lateinit var dialogError: AlertDialog.Builder

    // Asincronismo
    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + parentJob)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_contratar, container, false)

        // Elementos de la vista
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
        // Get del asesor mediante argumentos
        asesor = FragmentHRContratarArgs.fromBundle(requireArguments()).asesor

        // Shared Preferences para buscar el user hr en sesion
        val sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )
        val uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        // Asincronismo
        scope.launch {
            userHr = userService.findByID(uidKey)!!
            setRecyclerView()
        }

        // Llenamos la vista de los datos del usuario dev
        setUserData()

        // Listeners de los botones
        btnVolver.setOnClickListener {
            v.findNavController()
                .navigate(FragmentHRContratarDirections.actionFragmentHRContratarToFragmentHRHome())
        }

        btnContratar.setOnClickListener {
            getDateTimeCalendar()
            tecnologiasConsultadas = obtenerTecnologiasActivas(userTecnologias)
            if (tecnologiasConsultadas.isEmpty()) {
                Snackbar.make(
                    v,
                    "Debe seleccionar al menos una tecnologia por la cual solicita al Asesor Tecnico",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                context?.let { it1 -> DatePickerDialog(it1, this, year, month, day).show() }
            }
        }
    }

    // Metodos de vista
    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        txtNombreCompleto.text = asesor.name + " " + asesor.lastName
        txtDescripcion.text = asesor.descripcion
        txtPrecio.text = "$" + asesor.precio
        txtTitulo.text = asesor.titulo
        txtSeniority.text = asesor.seniority
    }

    // Metodos para el Recycler View
    private suspend fun setRecyclerView() {
        tecnologias = tecnologiaService.getAllTecnologias()
        userTecnologias = buscarTecnologias(tecnologias, asesor.tecnologias)
        activity?.runOnUiThread {
            tecnologiaListAdapter =
                TecnologiaListAdapter(userTecnologias) { x -> onTecnologiaClick(x) }
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

    private fun onTecnologiaClick(position: Int): Boolean {
        val tecnologiaEnCuestion = userTecnologias[position]
        val isTecActiva = !tecnologiaEnCuestion.active
        tecnologiaEnCuestion.active = isTecActiva
        val action: String = if (isTecActiva) "agregado." else "removido."
        Snackbar.make(v, tecnologiaEnCuestion.text + " fue " + action, Snackbar.LENGTH_SHORT).show()
        tecnologiaListAdapter =
            TecnologiaListAdapter(userTecnologias) { x -> onTecnologiaClick(x) }
        recTecnologias.adapter = tecnologiaListAdapter
        return true
    }

    private fun obtenerTecnologiasActivas(tecUser: MutableList<Tecnologia>): List<String> {
        return tecUser.filter { tecnologia -> tecnologia.active }
            .map { tecActiva -> tecActiva.text }
    }

    // Metodos de Dia y Hora
    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minutes = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        fechaEntrevista = LocalDate.of(year, Month.of(month + 1), dayOfMonth)

        if (fechaEntrevista.isBefore(LocalDate.now().plusDays(1))) {
            crearDialogError("Debe seleccionar una fecha posterior a hoy para realizar la solicitud de entrevista.")
            dialogError.show()
        } else {
            getDateTimeCalendar()
            TimePickerDialog(context, this, hour, minutes, true).show()
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        horaEntrevista = LocalTime.of(hourOfDay, minute)

        horaEntrevista.isAfter(LocalTime.of(7, 0))
        if (horaEntrevista.isAfter(LocalTime.of(7, 0)) && horaEntrevista.isBefore(
                LocalTime.of(
                    20,
                    0
                )
            )
        ) {
            fechaHoraEntrevista = LocalDateTime.of(fechaEntrevista, horaEntrevista)
            dialogDuracion()
        } else {
            crearDialogError("Debe seleccionar un horario entre las 7:00 AM y 20:00 PM para realizar la solicitud de entrevista.")
            dialogError.show()
        }
    }

    // Metodos de Dialog Duracion
    private fun dialogDuracion() {
        crearDialogDuracion()
        dialogDuracion.show()
    }

    private fun crearDialogDuracion() {
        dialogDuracion = AlertDialog.Builder(context)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_number_picker, null)

        dialogDuracion.setTitle("Duración")
        dialogDuracion.setMessage("Seleccione la duración en horas para la entrevista")
        dialogDuracion.setView(dialogView)

        val numberPicker: NumberPicker = dialogView.findViewById(R.id.number_picker)
        numberPicker.maxValue = 3
        numberPicker.minValue = 1
        numberPicker.wrapSelectorWheel = false

        dialogDuracion.setPositiveButton("Confirmar") { _, _ ->
            duracion = numberPicker.value
            precio = duracion as Int * asesor.precio.toInt()
            dialogConfirmarEntrevista()
        }
        dialogDuracion.setNegativeButton("Cancelar") { _, _ -> }
    }

    // Metodos de Dialog Entrevista
    private fun dialogConfirmarEntrevista() {
        val entrevista = crearEntrevista()
        crearDialogConfirmar(entrevista)
        dialogContratar.show()
    }

    private fun crearDialogConfirmar(entrevista: Entrevista) {
        val stringEntrevista =
            "Dia: ${entrevista.fecha} \nDuración: ${entrevista.duracion} HS \nPrecio: \$${precio} \nTecnologias consultadas:${listarTecnologias()} "
        dialogContratar = AlertDialog.Builder(this.context)
        dialogContratar.setTitle("Desea confirmar la siguiente entrevista?");
        dialogContratar.setMessage(stringEntrevista);
        dialogContratar.setPositiveButton("Confirmar") { _, _ ->
            scope.launch {
                entrevistaService.crearEntrevista(entrevista)
                Snackbar.make(v, "Entrevista confirmada.", Snackbar.LENGTH_SHORT).show()
            }
        }
        dialogContratar.setNegativeButton("Cancelar") { _, _ ->
            Snackbar.make(v, "Entrevista cancelada.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun listarTecnologias(): String {
        var stringTecnologias: String = ""
        for (tec in tecnologiasConsultadas) {
            stringTecnologias = "$stringTecnologias\n - $tec"
        }
        return stringTecnologias
    }

    // Metodos de Dialog Error
    private fun crearDialogError(mensaje: String) {
        dialogError = AlertDialog.Builder(this.context);
        dialogError.setTitle("Hay un problema...")
        dialogError.setMessage(mensaje);
        dialogError.setPositiveButton("Ok") { _, _ ->
        }
    }

    // Metodos de Entrevista
    private fun crearEntrevista(): Entrevista {
        return Entrevista(
            "",
            "${userHr.name} ${userHr.lastName}",
            userHr.empresa,
            asesor.id,
            userHr.id,
            fechaHoraEntrevista.format(formatter).toString(),
            duracion as Int,
            (1..5).random(),
            precio as Int,
            Entrevista.Constants.estadoPendienteRespuesta,
            "",
            tecnologiasConsultadas
        )
    }
}
