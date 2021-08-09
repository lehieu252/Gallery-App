package com.example.galleryview.views

import android.app.Activity
import android.app.AlertDialog
import com.example.galleryview.R

class LoadingDialog(val context: Activity) {
    private var dialog: AlertDialog

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val inflater = context.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog, null))
        dialog = builder.create()
    }

    fun start() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}