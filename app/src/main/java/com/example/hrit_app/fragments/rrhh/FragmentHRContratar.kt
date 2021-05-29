package com.example.hrit_app.fragments.rrhh

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.TecnologiaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class FragmentHRContratar : Fragment() {

    // View y ViewModel
    // private lateinit var viewModel: FragmentHRContratarViewModel
    private lateinit var v: View

    // XML
    private lateinit var txtNombreCompleto: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtTitulo: TextView
    private lateinit var txtPrecio: TextView
    private lateinit var btnContratar: Button
    private lateinit var btnVolver: ImageView

    // RecyclerView
    private lateinit var recTecnologias: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var tecnologiaListAdapter: TecnologiaListAdapter

    // Asesor
    private lateinit var asesor: User

    var tecnologiaService: TecnologiaService = TecnologiaService()
    var tecnologias: MutableList<Tecnologia> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_contratar, container, false)

        txtNombreCompleto = v.findViewById(R.id.txtNombreCompleto)
        txtDescripcion = v.findViewById(R.id.txtDescripcion)
        txtPrecio = v.findViewById(R.id.txtPrecio)
        txtTitulo = v.findViewById(R.id.txtTitulo)
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
            v.findNavController().navigate(FragmentHRContratarDirections.actionFragmentHRContratarToFragmentHRHome())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        txtNombreCompleto.text = asesor.name + " " + asesor.lastName
        txtDescripcion.text = asesor.descripcion
        txtPrecio.text = "$" + asesor.precio
        txtTitulo.text = asesor.titulo
    }

    suspend fun setRecyclerView() {
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

}