package com.example.galleryview.views

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.galleryview.R

class CreateAlbumDialog(val context: Activity) {
    private var dialog: AlertDialog

    init {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialog = builder.create()
        val view = context.layoutInflater.inflate(R.layout.create_album_dialog, null)
        dialog.setView(view)
        dialog.setCancelable(false)
        val editText = view.findViewById<TextView>(R.id.album_create_text)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_dialog_btn)
        val createBtn = view.findViewById<Button>(R.id.create_dialog_btn)

        createBtn.setOnClickListener {
            Toast.makeText(context, "Create ${editText.text}", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener{
            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
//        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Create") { dialog, which ->
//            Toast.makeText(context, "Create ${editText.text}", Toast.LENGTH_SHORT).show()
//        }
//        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, which ->
//            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
//            dismiss()
//        }
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}