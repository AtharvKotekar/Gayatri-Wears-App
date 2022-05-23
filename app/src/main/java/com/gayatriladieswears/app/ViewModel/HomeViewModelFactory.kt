package com.gayatriladieswears.app.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gayatriladieswears.app.Fragments.HomeFragment

class HomeViewModelFactory(private val fragment: HomeFragment): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(fragment) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}