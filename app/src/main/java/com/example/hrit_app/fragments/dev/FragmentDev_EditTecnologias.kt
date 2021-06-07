package com.example.hrit_app.fragments.dev

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hrit_app.R
import com.example.hrit_app.adapters.TecnologiaListAdapter
import com.example.hrit_app.entities.Tecnologia
import com.example.hrit_app.entities.User
import com.example.hrit_app.fragments.rrhh.FragmentHRContratarArgs
import com.example.hrit_app.services.TecnologiaService
import com.example.hrit_app.services.UserService
import com.example.hrit_app.utils.constants.Categoria
import com.example.hrit_app.utils.constants.Rol
import com.example.hrit_app.utils.constants.Seniority
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.gms.common.util.CollectionUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_hr__home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentDev_EditTecnologias : Fragment() {

    lateinit var v: View
    lateinit var recTecnologias: RecyclerView
    var tecnologiasParaAdapter: MutableList<Tecnologia> = ArrayList<Tecnologia>()
    var tecnologiasFiltradasPorCategoria: MutableList<Tecnologia> = ArrayList<Tecnologia>()
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var tecnologiaListAdapter: TecnologiaListAdapter
    var tecnologiaService: TecnologiaService = TecnologiaService()
    private lateinit var user: User
    lateinit var btnTecnologias: Button
    lateinit var spinner: Spinner
    private lateinit var sharedPreferences: SharedPreferences
    var userService: UserService = UserService()
    lateinit var categoriaSeleccionada: String
    private lateinit var dialogContratar: AlertDialog.Builder


    val mapCategoriaTecnologia: Map<Int, String> = mapOf( Pair(1, Categoria.BE),
        Pair(2, Categoria.FE),
        Pair(3, Categoria.MOBILE),
        Pair(4, Categoria.BD ));


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dev__edit_tecnologias, container, false)
        recTecnologias = v.findViewById(R.id.recTecnologias)
        btnTecnologias = v.findViewById(R.id.btnEditarTecnologias)
        spinner = v.findViewById(R.id.spinnerCategoriaTecnologia)

        // Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )
        return v
    }

    override fun onStart() {
        super.onStart()
        user = FragmentDev_EditTecnologiasArgs.fromBundle(requireArguments()).user
        recTecnologias.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Usamos asincronismo
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.Default + parentJob)
        scope.launch {
            tecnologiasParaAdapter = tecnologiaService.getAllTecnologias()
            activity?.runOnUiThread {
                activarTecnologias(user.tecnologias, tecnologiasParaAdapter)
                activity?.runOnUiThread {
                tecnologiaListAdapter = TecnologiaListAdapter(tecnologiasParaAdapter) { x -> onTecnologiaClick(x) }
                recTecnologias.layoutManager = linearLayoutManager
                recTecnologias.adapter = tecnologiaListAdapter
                }
            }
        }

        btnTecnologias.setOnClickListener {
            val tecnologiasActivasDelUsuario: List<String> = obtenerTecnologiasActivas(tecnologiasParaAdapter)
            if (tecnologiasActivasDelUsuario.isEmpty()) {
                Snackbar.make(
                    v,
                    "Debe seleccionar al menos una tecnologia para poder actualizar",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                crearDialogConfirmar(tecnologiasActivasDelUsuario);
            }
        }
        displaySpinner()
    }


    private fun obtenerTecnologiasActivas(tecnologiasParaAdapter: MutableList<Tecnologia>): List<String> {
        return tecnologiasParaAdapter.filter { tecnologia -> tecnologia.active }.map { tecActiva -> tecActiva.text }
    }

    private fun activarTecnologias(tecnologiasDelUsuario: List<String>, tecnologiasParaAdapter: MutableList<Tecnologia>) {
        for (tecnologiaUsuario in tecnologiasDelUsuario) {
            val tecFiltradas = tecnologiasParaAdapter.filter { tecnologia -> tecnologia.text.equals(tecnologiaUsuario) }
            val tecFiltrada = tecFiltradas.get(0)
            if (tecFiltrada != null) {
                tecFiltrada.active = true
            }
        }
    }

    private fun displayTecnologiasByCategoriaOnRecyclerView(categoria: String) {
        tecnologiasFiltradasPorCategoria = tecnologiasParaAdapter.filter { tecnologia -> categoria.equals(tecnologia.categoria)}.toMutableList()
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologiasFiltradasPorCategoria as MutableList<Tecnologia>, { x -> onTecnologiaClick(x) })
        recTecnologias.adapter = tecnologiaListAdapter
    }

    fun onTecnologiaClick(position: Int): Boolean {
        // Actualizo estado
        var tecnologiaEnCuestion: Tecnologia
        if (tecnologiasFiltradasPorCategoria.size == 0) {
            tecnologiaEnCuestion = tecnologiasParaAdapter.get(position)
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

        return true
    }

    private fun crearDialogConfirmar(tecnologiasSeleccionadas: List<String>) {
        val stringTecnologias =
            "Tus tecnologías ahora van a ser: ${listarTecnologias(tecnologiasSeleccionadas)} "
        dialogContratar = AlertDialog.Builder(this.context)
        dialogContratar.setTitle("¿Deseas actualizar tus tecnologías?");
        dialogContratar.setMessage(stringTecnologias);
        dialogContratar.setPositiveButton("Confirmar") { _, _ ->
            val uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()
            userService.updateTecnologiasUser(tecnologiasSeleccionadas , uidKey)
            Snackbar.make(v, "Las tecnologias del usuario han sido actualizadas.", Snackbar.LENGTH_SHORT).show()
        }
        dialogContratar.setNegativeButton("Cancelar") { _, _ ->
            Snackbar.make(v, "Las tecnologias del usuario no han sido actualizadas.", Snackbar.LENGTH_SHORT).show()
        }
        dialogContratar.show()
    }

    private fun listarTecnologias(tecnologiasSeleccionadas: List<String>): String {
        var stringTecnologias: String = ""
        for (tec in tecnologiasSeleccionadas) {
            stringTecnologias = "$stringTecnologias\n - $tec"
        }
        return stringTecnologias
    }

    private fun displayAllCategorias(){
        tecnologiaListAdapter = TecnologiaListAdapter(tecnologiasParaAdapter as MutableList<Tecnologia>, { x -> onTecnologiaClick(x) })
        recTecnologias.adapter = tecnologiaListAdapter
    }

    private fun displaySpinner(){
        var categorias = arrayOf("Seleccionar Categoria...", Categoria.BE, Categoria.FE, Categoria.MOBILE, Categoria.BD)
        spinner.adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            categorias
        )

        spinner.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener {
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

}