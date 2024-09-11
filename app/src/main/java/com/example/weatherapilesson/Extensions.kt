package com.example.weatherapilesson

import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import java.security.Permission

fun Fragment.isPermissionGranted(permission: String): Boolean {

    return ContextCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED
}

fun View.show(){
    isVisible = true
}

fun View.hide(){
    isVisible = false

}

fun View.showToTime(){
    isVisible = true
    postDelayed(3000){
        isVisible = false
    }
}