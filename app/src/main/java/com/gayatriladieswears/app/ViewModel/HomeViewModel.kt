package com.gayatriladieswears.app.ViewModel

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.gayatriladieswears.app.Adaptors.TopCategoriesAdaptor
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.gayatriladieswears.app.Model.Info
import com.gayatriladieswears.app.getData

class HomeViewModel(fragment: HomeFragment): ViewModel() {
    val fragment = fragment
    lateinit var topCategoryAdaptor:TopCategoriesAdaptor

    init {

        FirestoreClass().getTopCategories(fragment)
        FirestoreClass().getProducts(fragment,"tag", "Spotlight On")
        FirestoreClass().getSizes(fragment)
        FirestoreClass().getProducts(fragment,"tag", "Traditional")
        FirestoreClass().getFabrics(fragment)
        FirestoreClass().getProducts(fragment,"tag", "New Collection")
        FirestoreClass().getColors(fragment)
        FirestoreClass().getDeals(fragment)
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun infoGet(iteamList: ArrayList<Info>): TopCategoriesAdaptor {
        topCategoryAdaptor = TopCategoriesAdaptor(fragment.requireContext(),iteamList)
        return topCategoryAdaptor
    }
}