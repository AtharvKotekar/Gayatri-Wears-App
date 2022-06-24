package com.gayatriladieswears.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentOrderReturnBinding

class OrderReturnFragment : Fragment() {

    private lateinit var binding:FragmentOrderReturnBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderReturnBinding.inflate(inflater,container,false)



        return binding.root
    }


}