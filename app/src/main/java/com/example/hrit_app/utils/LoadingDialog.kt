package com.example.hrit_app.utils

import android.app.Activity
import android.app.AlertDialog
import android.os.Handler
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.example.hrit_app.R

class LoadingDialog(private val act: Activity) {
    lateinit var isDialog: AlertDialog

    fun startLoading() {
        val inflater = act.layoutInflater
        val dialogView = inflater.inflate(R.layout.item_loading, null)
        val builder = AlertDialog.Builder(act, R.style.dialog)
        // R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()

        val window: Window? = isDialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        );
        window?.setGravity(Gravity.CENTER);

        isDialog.show()
        Handler().postDelayed({ isDismiss() }, 1000)
    }

    private fun isDismiss() {
        isDialog.dismiss()
    }
}