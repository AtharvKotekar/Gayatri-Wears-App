package com.gayatriladieswears.app

import android.R
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.google.firebase.auth.*


fun Fragment.vibratePhone() {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

fun getData(fragment: HomeFragment) {
    FirestoreClass().getTopCategories(fragment)
    FirestoreClass().getProducts(fragment,"tag", "Spotlight On")
    FirestoreClass().getSizes(fragment)
    FirestoreClass().getProducts(fragment,"tag", "Traditional")
    FirestoreClass().getFabrics(fragment)
    FirestoreClass().getProducts(fragment,"tag", "New Collection")
    FirestoreClass().getColors(fragment)
    FirestoreClass().getDeals(fragment)
}












