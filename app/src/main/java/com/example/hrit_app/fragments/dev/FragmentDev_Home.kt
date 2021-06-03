package com.example.hrit_app.fragments.dev

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.EntrevistaListAdapter
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.services.EntrevistaService
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentDev_Home : Fragment() {


    lateinit var v: View
    lateinit var uidKey: String
    lateinit var recEntrevistasPendientes : RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var emptyView : TextView


    var entrevistasPendientes : MutableList<Entrevista> = ArrayList<Entrevista>()
    private lateinit var entrevistaListAdapter: EntrevistaListAdapter
    var entrevistaService: EntrevistaService = EntrevistaService()

    companion object {
        fun newInstance() = FragmentDev_Home()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v =  inflater.inflate(R.layout.fragment_dev_home, container, false)
        recEntrevistasPendientes = v.findViewById(R.id.rec_entrevistasPendientes)
        emptyView = v.findViewById(R.id.empty_view)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )

        uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

        recEntrevistasPendientes.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recEntrevistasPendientes.layoutManager = linearLayoutManager

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            entrevistasPendientes = entrevistaService.findAllEntrevistasPendientesByDev(uidKey)
            activity?.runOnUiThread {
                actualizarEntrevistas(entrevistasPendientes)
            }
        }


        entrevistaListAdapter = EntrevistaListAdapter(entrevistasPendientes, { x ->
            onAceptarEntrevistaClick(x)
        }, {
            x -> onRechazarEntrevistaClick(x)
        })

        recEntrevistasPendientes.adapter = entrevistaListAdapter

    }


    fun confirmarAccion(texto: String, x : Int, estado : String ){
        val input = EditText(this.context)
        input.visibility = View.INVISIBLE
        val mAlertDialog = AlertDialog.Builder(this.context);
        input.hint = "Ingrese sus comentarios"
        mAlertDialog.setTitle("Confirmar accion");
        mAlertDialog.setMessage(texto);
        if(estado == Entrevista.Constants.estadoRechazada) {
            mAlertDialog.setView(input)
            input.visibility = View.VISIBLE
        }
        mAlertDialog.setPositiveButton("Cancelar") { dialog, id ->
            Snackbar.make(v, "Accion cancelada", Snackbar.LENGTH_SHORT).show()
        }
        mAlertDialog.setNegativeButton("Aceptar") { dialog, id ->
            Snackbar.make(v, "Accion aceptada", Snackbar.LENGTH_SHORT).show()
            entrevistaService.updateEntrevistaEstado(entrevistasPendientes[x].id, estado)
            if(estado == Entrevista.Constants.estadoRechazada) {
                entrevistaService.updateEntrevistaComentarios(entrevistasPendientes[x].id, input.text.toString())
            }
            val parentJob = Job()
            val scope = CoroutineScope(Dispatchers.Default + parentJob)
            scope.launch {
                entrevistasPendientes = entrevistaService.findAllEntrevistasPendientesByDev(uidKey)
                activity?.runOnUiThread {
                    actualizarEntrevistas(entrevistasPendientes)
                }

            }
        }
        mAlertDialog.show();

    }

    fun onAceptarEntrevistaClick (x : Int) : Boolean {
        var estado = Entrevista.Constants.estadoAceptado;
        var mensaje = "Estas por aceptar una entrevista"
        this.confirmarAccion(mensaje, x, estado);
        return true
    }


    fun onRechazarEntrevistaClick (x : Int) : Boolean {
        var estado = Entrevista.Constants.estadoRechazada;
        var mensaje = "Estas por rechazar una entrevista"
        this.confirmarAccion(mensaje, x, estado);
        return true
    }

    private fun actualizarEntrevistas(entrevistasPendientes: MutableList<Entrevista>){
        entrevistaListAdapter = EntrevistaListAdapter(entrevistasPendientes, { x ->
            onAceptarEntrevistaClick(x)
        }, {
            x -> onRechazarEntrevistaClick(x)
        })
        recEntrevistasPendientes.adapter = entrevistaListAdapter
        if (entrevistasPendientes.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            emptyView.setTextColor((0xff000000).toInt())
            recEntrevistasPendientes.visibility = View.INVISIBLE
        } else {
            emptyView.visibility = View.INVISIBLE
            recEntrevistasPendientes.visibility = View.VISIBLE
        }
    }





}