package com.gayatriladieswears.app

import android.R
import android.animation.AnimatorSet
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.gayatriladieswears.app.Model.AWBResult
import com.gayatriladieswears.app.Model.GenrateAWB
import com.gayatriladieswears.app.Model.Product
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.google.firebase.firestore.SetOptions
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()


fun Fragment.vibratePhone() {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

suspend fun getData(fragment: HomeFragment) {
    CoroutineScope(IO).launch { FirestoreClass().getTopCategories(fragment) }
    CoroutineScope(IO).launch { FirestoreClass().getProducts(fragment,"tag", "Spotlight On") }
    CoroutineScope(IO).launch { FirestoreClass().getSizes(fragment) }
    CoroutineScope(IO).launch { FirestoreClass().getProducts(fragment,"tag", "Traditional") }
    CoroutineScope(IO).launch { FirestoreClass().getFabrics(fragment) }
    CoroutineScope(IO).launch { FirestoreClass().getProducts(fragment,"tag", "New Collection") }
    CoroutineScope(IO).launch { FirestoreClass().getColors(fragment) }
    CoroutineScope(IO).launch { FirestoreClass().getDeals(fragment) }

}

fun getRandomString(length: Int) : String {
    val ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val random = Random()
    val sb = StringBuilder(length)
    for (i in 0 until length)
        sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
    return sb.toString()
}






