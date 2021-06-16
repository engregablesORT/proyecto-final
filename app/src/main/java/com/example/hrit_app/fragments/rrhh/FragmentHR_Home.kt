package com.example.hrit_app.fragments.rrhh

import android.app.AlertDialog
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
import com.example.hrit_app.utils.constants.Seniority
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_hr_home.view.*
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
    lateinit var textTecnologia: TextView
    lateinit var trash: ImageView
    private lateinit var dialogLimpiarFiltros: AlertDialog.Builder
    private lateinit var chipGroup: ChipGroup


    val mapCategoriaTecnologia: Map<Int, String> = mapOf( Pair(1, Categoria.BE),
        Pair(2, Categoria.FE),
        Pair(3, Categoria.MOBILE),
        Pair(4, Categoria.BD ));

    val mapSeniorityChip: Map<String, String> = mapOf( Pair("JR", Seniority.JR),
        Pair("SSR", Seniority.SSR),
        Pair("SR", Seniority.SR),
        Pair("LT", Seniority.TL),
        Pair("ALL", "ALL"))

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
        textTecnologia = v.findViewById(R.id.home_hr_text_tecnologias)
        trash = v.findViewById(R.id.trashFilter)
        chipGroup = v.findViewById(R.id.seniority_chips_2)

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
                userFilter.name = newText
                findAsesoresTecnicosByFilter(userFilter)
                return false
            }
        })

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val senioritySeleccionado = v.findViewById<Chip>(checkedId).text.toString()
            val seniority = mapSeniorityChip.get(senioritySeleccionado);
            if (seniority != null && !seniority.equals("ALL")) {
                userFilter.seniority = seniority
            } else {
                userFilter.seniority = ""
            }
            findAsesoresTecnicosByFilter(userFilter)
        }

        displaySpinner()

        trash.setOnClickListener{
            crearDialogConfirmarLimpiarFiltros()
        }
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
        // Obtengo la tecnologia en cuestion
        var tecnologiaEnCuestion: Tecnologia
        if (tecnologiasFiltradasPorCategoria.size == 0) {
            tecnologiaEnCuestion = tecnologias.get(position)
        } else {
            tecnologiaEnCuestion = tecnologiasFiltradasPorCategoria.get(position)
        }
        // Actualizo su estado para mostrar o no si esta activa
        val isTecActiva = !tecnologiaEnCuestion.active
        tecnologiaEnCuestion.active = isTecActiva
        val action: String = if (isTecActiva) "agregado." else "removido."
        Snackbar.make(v, tecnologiaEnCuestion.text + " fue " + action, Snackbar.LENGTH_SHORT).show()

        // Lo reflejo en la lista
        if (categoriaSeleccionada.length>0){
            displayTecnologiasByCategoriaOnRecyclerView(tecnologiaEnCuestion.categoria)
        } else {
            displayAllCategorias()
        }

        // Get tecnologias activas
        val tecnologiasActivas = obtenerTecnologiasActivas(tecnologias)
        userFilter.tecnologias = tecnologiasActivas
        findAsesoresTecnicosByFilter(userFilter);
        return true
    }

    private fun findAsesoresTecnicosByFilter(userFilter: User){
        asesoresTecnicosFiltrados = asesoresTecnicos.filter { usuario ->
                usuario.seniority.contains(userFilter.seniority)
                && usuario.tecnologias.containsAll(userFilter.tecnologias)
                && (usuario.name.toUpperCase().contains(userFilter.name.toUpperCase()) ||
                    usuario.lastName.toUpperCase().contains(userFilter.name.toUpperCase()))
        }.toMutableList()

        actualizarListaDelRecyclerViewDeAsesores(asesoresTecnicosFiltrados)
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
                    textTecnologia.visibility = View.VISIBLE
                    displayTecnologiasByCategoriaOnRecyclerView(categoria)
                    categoriaSeleccionada = categoria
                } else {
                    recTecnologias.visibility = View.INVISIBLE
                    textTecnologia.visibility = View.INVISIBLE
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

    private fun crearDialogConfirmarLimpiarFiltros() {
        dialogLimpiarFiltros = AlertDialog.Builder(this.context)
        dialogLimpiarFiltros.setTitle("¿Deseas limpiar los filtros de búsqueda?");
        dialogLimpiarFiltros.setMessage("Si presiona Confirmar, volverá al estado inicial.")
        dialogLimpiarFiltros.setPositiveButton("Confirmar") { _, _ ->
            userFilter = User()
            resetearSpinner()
            resetearChips()
            resetSearchView()
            findAsesoresTecnicosByFilter(userFilter);
        }
        dialogLimpiarFiltros.setNegativeButton("Cancelar") { _, _ ->
            // TODO ... no hacer nada?
        }
        dialogLimpiarFiltros.show()

    }

    private fun resetearSpinner(){
        tecnologiasFiltradasPorCategoria = arrayListOf()
        recTecnologias.visibility = View.INVISIBLE
        textTecnologia.visibility = View.INVISIBLE
        tecnologias.forEach({tec-> tec.active = false})
        spinnerCategorias.setSelection(0)

    }

    private fun resetearChips(){
        chipGroup.chip_jr.isChecked = false
        chipGroup.chip_ssr.isChecked =  false
        chipGroup.chip_sr.isChecked = false
        chipGroup.chip_tl.isChecked = false
        chipGroup.chip_all.isChecked = true
    }

    private fun resetSearchView(){
        searchView.setQuery("", false)
    }

}
