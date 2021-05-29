package com.example.hrit_app.fragments.rrhh

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.User
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

    // RecyclerView
    private lateinit var recTecnologias: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var tecnologiaListAdapter: TecnologiaListAdapter

    // Asesor
    private lateinit var asesor: User

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
        recTecnologias = v.findViewById(R.id.recTecnologiasContratar)

        return v
    }

    override fun onStart() {
        super.onStart()
        asesor = FragmentHRContratarArgs.fromBundle(requireArguments()).asesor
        setUserData()
        Log.d("TEST", asesor.email)
        Log.d("TEST", asesor.descripcion)

        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setUserData() {
        txtNombreCompleto.text = asesor.name + " " + asesor.lastName
        txtDescripcion.text = asesor.descripcion
        txtPrecio.text = "$" + asesor.precio
        txtTitulo.text = asesor.titulo
    }
}