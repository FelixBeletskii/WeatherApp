package com.example.weatherapilesson

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

object DialogManger {

    fun locationsSettingDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Location disabled")
        dialog.setMessage("Do you wanna enable GPS?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { _, _ ->
            dialog.dismiss()
            listener.onClick(null)
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()

        }
        dialog.show()

    }
    fun searchByName(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val edName = EditText(context)
        builder.setView(edName )
        val dialog = builder.create()
        dialog.setTitle("City name")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { _, _ ->
            dialog.dismiss()
            listener.onClick(edName.text.toString())
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()

        }
        dialog.show()

    }


    interface Listener {
        fun onClick(name: String?)
    }
}