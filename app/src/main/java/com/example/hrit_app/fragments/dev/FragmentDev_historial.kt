package com.example.hrit_app.fragments.dev

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.EntrevistaListAdapter
import com.example.hrit_app.adapters.TransaccionListAdapter
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.services.EntrevistaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.LoadingDialog
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import kotlinx.android.synthetic.main.item_transaccion.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentDev_historial : Fragment() {

    private lateinit var v: View

    private lateinit var txtEntrevistasTotales: TextView
    private lateinit var txtEntrevistasRechazadas: TextView
    private lateinit var txtGananciasTotales: TextView
    private lateinit var txtValoracion : TextView

    private var entrevistasFinalizadas : MutableList<Entrevista> = ArrayList<Entrevista>()
    private var entrevistasTotales : MutableList<Entrevista> = ArrayList<Entrevista>()
    private var entrevistaService: EntrevistaService = EntrevistaService()

    private var valoracionDev = 0.0
    private var userService : UserService = UserService()

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var recTransacciones: RecyclerView
    private lateinit var transaccionListAdapter: TransaccionListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var dialogLoading: LoadingDialog

    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + parentJob)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_dev_historial, container, false)

        txtEntrevistasTotales = v.findViewById(R.id.historialdev_entrevistas_totales)
        txtEntrevistasRechazadas = v.findViewById(R.id.historialhr_entrevistas_rechazadas)
        txtGananciasTotales = v.findViewById(R.id.historialdev_ganancias_totales)
        txtValoracion = v.findViewById(R.id.historialdev_valoracion)
        recTransacciones = v.findViewById(R.id.rec_transacciones)

        sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )

        dialogLoading = activity?.let { LoadingDialog(it) }!!
        dialogLoading.cargando()

        return v
    }

    override fun onStart() {
        super.onStart()

        val uid = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

        recTransacciones.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recTransacciones.layoutManager = linearLayoutManager


        scope.launch {
            entrevistasFinalizadas = entrevistaService.findAllEntrevistasFinalizadasByDev(uid)
            entrevistasTotales = entrevistaService.findAllEntrevistasByDEV(uid)
            valoracionDev = userService.findByID(uid)?.valoracion!!

            activity?.runOnUiThread {
                actualizarEntrevistas(entrevistasFinalizadas)
                setTextosEntrevistas()
                setValoracion()
                dialogLoading.terminarCargando()
            }
        }

        transaccionListAdapter = TransaccionListAdapter(entrevistasFinalizadas)
        recTransacciones.adapter = transaccionListAdapter
    }

    private fun setTextosEntrevistas() {
        txtEntrevistasTotales.text =
            entrevistasTotales.size.toString()
        txtEntrevistasRechazadas.text =
            filtrarEntrevistasPorEstado(
                entrevistasTotales,
                Entrevista.Constants.estadoRechazada
            ).size.toString()
        txtGananciasTotales.text =
            "$ " + sumarTotalGanancias(entrevistasFinalizadas).toString()
    }

    private fun setValoracion() {
        txtValoracion.text = valoracionDev.toString()
        view?.findViewById<RatingBar>(R.id.ratingBar)!!.rating = valoracionDev.toFloat()
    }

    private fun filtrarEntrevistasPorEstado(
        entrevistas: MutableList<Entrevista>,
        estado: String
    ): MutableList<Entrevista> {
        return entrevistas.filter { entrevista -> entrevista.estado == estado } as MutableList<Entrevista>
    }

    private fun sumarTotalGanancias( entrevistas: MutableList<Entrevista>): Int {
        var total = 0
        entrevistas.forEach { entrevista: Entrevista -> total += entrevista.precio }
        return total
    }

    private fun actualizarEntrevistas(entrevistasFinalizadas: MutableList<Entrevista>){
        transaccionListAdapter = TransaccionListAdapter(entrevistasFinalizadas)
        recTransacciones.adapter = transaccionListAdapter
    }

}