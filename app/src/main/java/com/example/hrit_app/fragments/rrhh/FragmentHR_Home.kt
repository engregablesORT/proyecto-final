package com.example.hrit_app.fragments.rrhh

import android.graphics.Color
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
import com.example.hrit_app.adapters.AsesorTecnicoListAdapter
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.services.TecnologiaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.Categoria
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FragmentHR_Home : Fragment() {

    lateinit var v: View
    lateinit var recTecnologias: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var tecnologiaListAdapter: TecnologiaListAdapter
    lateinit var searchView: SearchView
    var tecnologias: MutableList<Tecnologia> = ArrayList<Tecnologia>()
    var tecnologiaService: TecnologiaService = TecnologiaService()
    var tecnologiasFiltradasPorCategoria: MutableList<Tecnologia> = ArrayList<Tecnologia>()

    lateinit var recAsesores: RecyclerView
    lateinit var linearLayoutManagerAsesores: LinearLayoutManager
    lateinit var asesoresListAdapter: AsesorTecnicoListAdapter
    var asesoresTecnicos: MutableList<User> = mutableListOf()
    var asesoresTecnicosFiltrados: MutableList<User> = mutableListOf()
    var userService: UserService = UserService()
    lateinit var spinnerCategorias : Spinner
    lateinit var categoriaSeleccionada: String
    var userFilter = User()

    val mapCategoriaTecnologia: Map<Int, String> = mapOf( Pair(1, Categoria.BE),
        Pair(2, Categoria.FE),
        Pair(3, Categoria.MOBILE),
        Pair(4, Categoria.BD ));

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_home, container, false)
        // Inflate the layout for this fragment
        recTecnologias = v.findViewById(R.id.recTecnologias)
        recAsesores = v.findViewById(R.id.recAsesoresTecnicos)
        searchView = v.findViewById(R.id.searchView)
        spinnerCategorias = v.findViewById(R.id.homeHrCategoriasTec)

        return v
    }


    override fun onStart() {
        super.onStart()

        // creamos recycler asesores
        recAsesores.setHasFixedSize(true)
        linearLayoutManagerAsesores = LinearLayoutManager(context)

        // creamos recycler tecnologia
        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Cargamos los valores tanto para los asesores como para las tecnologias
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            asesoresTecnicos = userService.findAllAsesoresTecnicos()
            tecnologias = tecnologiaService.getAllTecnologias()
            activity?.runOnUiThread {
                actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos)
                setTecnolgias(tecnologias)
            }

        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }


            override fun onQueryTextChange(newText: String): Boolean {
                val parentJob = Job()
                val scope = CoroutineScope(Dispatchers.Default + parentJob)
                scope.launch {
                    if (newText.replace(" ", "").length > 0) {
                        asesoresTecnicos = userService.findByNombre(newText, asesoresTecnicos)
                    } else {
                        asesoresTecnicos = userService.findAllAsesoresTecnicos()
                    }
                    activity?.runOnUiThread {
                        actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos)
                    }
                }
                return false
            }
        })

        displaySpinner()

    }

    private fun actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicos: MutableList<User>) {
        asesoresListAdapter = AsesorTecnicoListAdapter(asesoresTecnicos, { x -> onAsesorClick(x) })
        recAsesores.layoutManager = linearLayoutManagerAsesores
        recAsesores.adapter = asesoresListAdapter
    }

    private fun setTecnolgias(tecnologias: MutableList<Tecnologia>) {
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologias, { x -> onTecnologiaClick(x) })
        recTecnologias.layoutManager = linearLayoutManager
        recTecnologias.adapter = tecnologiaListAdapter
    }

    fun onTecnologiaClick(position: Int): Boolean {
        // Actualizo estado
        var tecnologiaEnCuestion: Tecnologia
        if (tecnologiasFiltradasPorCategoria.size == 0) {
            tecnologiaEnCuestion = tecnologias.get(position)
        } else {
            tecnologiaEnCuestion = tecnologiasFiltradasPorCategoria.get(position)
        }
        val isTecActiva = !tecnologiaEnCuestion.active
        tecnologiaEnCuestion.active = isTecActiva
        // Lo reflejo en la lista
        val action: String = if (isTecActiva) "agregado." else "removido."
        Snackbar.make(v, tecnologiaEnCuestion.text + " fue " + action, Snackbar.LENGTH_SHORT).show()
        if (categoriaSeleccionada.length>0){
            displayTecnologiasByCategoriaOnRecyclerView(tecnologiaEnCuestion.categoria)
        } else {
            displayAllCategorias()
        }

        // Get tecnologias activas
        val tecnologiasActivas = obtenerTecnologiasActivas(tecnologias)
        userFilter.tecnologias = tecnologiasActivas

        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            asesoresTecnicosFiltrados = userService.findByUsuarioFilter(userFilter)
            activity?.runOnUiThread {
                actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicosFiltrados)
            }
        }
        return true
    }

    private fun onAsesorClick(position: Int): Boolean {
        var asesor: User
        if (asesoresTecnicosFiltrados.isNotEmpty()){
            asesor = asesoresTecnicosFiltrados[position]
        } else {
            asesor = asesoresTecnicos[position]
        }
        val action = FragmentHR_HomeDirections.actionFragmentHRHomeToFragmentHRContratar(asesor)
        v.findNavController().navigate(action)
        return true
    }

    private fun displaySpinner(){
        var categorias = arrayOf("Seleccionar Categoria...", Categoria.BE, Categoria.FE, Categoria.MOBILE, Categoria.BD)
        spinnerCategorias.adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            categorias
        )

        spinnerCategorias.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent?.getChildAt(0) != null){
                    val spinnerTextView = parent.getChildAt(0) as TextView
                    spinnerTextView.setTextColor(Color.WHITE)
                }
                categoriaSeleccionada = ""
                val categoria= mapCategoriaTecnologia.get(position);
                if (categoria != null) {
                    recTecnologias.visibility = View.VISIBLE
                    displayTecnologiasByCategoriaOnRecyclerView(categoria)
                    categoriaSeleccionada = categoria
                } else {
                    recTecnologias.visibility = View.INVISIBLE
                }

            }
        }
    }

    private fun displayTecnologiasByCategoriaOnRecyclerView(categoria: String) {
        tecnologiasFiltradasPorCategoria = tecnologias.filter { tecnologia -> categoria.equals(tecnologia.categoria)}.toMutableList()
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologiasFiltradasPorCategoria as MutableList<Tecnologia>, { x -> onTecnologiaClick(x) })
        recTecnologias.adapter = tecnologiaListAdapter
    }

    private fun displayAllCategorias(){
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologias as MutableList<Tecnologia>, { x -> onTecnologiaClick(x) })
        recTecnologias.adapter = tecnologiaListAdapter
    }

    private fun obtenerTecnologiasActivas(tecnologias: MutableList<Tecnologia>): List<String> {
        return tecnologias.filter { tecnologia -> tecnologia.active }.map { tecActiva -> tecActiva.text }
    }


}
