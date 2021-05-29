package com.example.hrit_app.fragments.rrhh

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentHRContratarViewModel : ViewModel() {

    //var asesor = FragmentHRContratarArgs.fromBundle(requireArguments()).asesor
    val name = MutableLiveData<String>()

    fun setData (){
        name.value = "pepe"
    }
}