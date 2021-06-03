package com.example.hrit_app.fragments.rrhh

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.hrit_app.R
import com.example.hrit_app.entities.Entrevista
import com.example.hrit_app.services.EntrevistaService
import com.example.hrit_app.utils.constants.SharedPreferencesKey
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentHRCalendario : Fragment() {

    lateinit var v: View
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var entrevistas: MutableList<Entrevista> = ArrayList<Entrevista>()
    private var entrevistaService: EntrevistaService = EntrevistaService()

    // SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    // Asincronismo
    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + parentJob)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_hr_calendario, container, false)

        tabLayout = v.findViewById(R.id.tab_layout)
        viewPager = v.findViewById(R.id.view_pager)

        // Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences(
            SharedPreferencesKey.PREF_NAME,
            Context.MODE_PRIVATE
        )

        return v
    }

    override fun onStart() {
        super.onStart()

        val uidKey = sharedPreferences.getString(SharedPreferencesKey.UID, "").toString()

        scope.launch {
            entrevistas = entrevistaService.findAllEntrevistasByHR(uidKey)
            activity?.runOnUiThread {
                viewPager.adapter = ViewPagerAdapter(requireActivity(), entrevistas)
                TabLayoutMediator(
                    tabLayout,
                    viewPager,
                    TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        when (position) {
                            0 -> tab.text = "Aceptadas"
                            1 -> tab.text = "Rechazadas"
                            2 -> tab.text = "Pendientes"
                        }
                    }).attach()
            }
        }
    }

    // Le paso la lista de las entrevistas y por dentro crea los fragmentes segun donde me ponga en el tab.
    // Cuando crea el fragment le envia la lista filtrada segun estado
    // Los distintos fragments en el constructor reciben una lista de entrevistas
    class ViewPagerAdapter(
        fragmentActivity: FragmentActivity,
        entrevistas: MutableList<Entrevista>
    ) :
        FragmentStateAdapter(fragmentActivity) {

        private var listaEntrevistas = entrevistas
        override fun createFragment(position: Int): Fragment {

            return when (position) {
                0 -> FragmentHRCalendarioEstado(
                    filtrarEntrevistasPorEstado(
                        listaEntrevistas,
                        Entrevista.Constants.estadoAceptado
                    )
                )
                1 -> FragmentHRCalendarioEstado(
                    filtrarEntrevistasPorEstado(
                        listaEntrevistas,
                        Entrevista.Constants.estadoRechazada
                    )
                )
                2 -> FragmentHRCalendarioEstado(
                    filtrarEntrevistasPorEstado(
                        listaEntrevistas,
                        Entrevista.Constants.estadoPendienteRespuesta
                    )
                )


                else -> FragmentHRCalendarioEstado(
                    filtrarEntrevistasPorEstado(
                        listaEntrevistas,
                        Entrevista.Constants.estadoAceptado
                    )
                )
            }
        }

        override fun getItemCount(): Int {
            return TAB_COUNT
        }

        companion object {
            private const val TAB_COUNT = 3
        }

        private fun filtrarEntrevistasPorEstado(
            entrevistas: MutableList<Entrevista>,
            estado: String
        ): MutableList<Entrevista> {
            return entrevistas.filter { entrevista -> entrevista.estado == estado } as MutableList<Entrevista>
        }
    }

}