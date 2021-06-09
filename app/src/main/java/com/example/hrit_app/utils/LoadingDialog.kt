package com.example.hrit_app.utils

import android.app.Activity
import android.app.AlertDialog
import android.os.Handler
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.example.hrit_app.R

class LoadingDialog(private val act: Activity) {
    private lateinit var dialog: AlertDialog

    fun cargando() {
        val inflater = act.layoutInflater
        val dialogView = inflater.inflate(R.layout.item_loading, null)
        val builder = AlertDialog.Builder(act, R.style.LoadingTheme)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

        // R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen
        // Handler().postDelayed({ isDismiss() }, 1000)
    }

    fun terminarCargando() {
        dialog.dismiss()
    }
}