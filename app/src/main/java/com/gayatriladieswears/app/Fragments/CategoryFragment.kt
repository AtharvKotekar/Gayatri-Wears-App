package com.gayatriladieswears.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment() {
    private lateinit var binding:FragmentCategoryBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(inflater,container,false)



        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.categoryFragment) {
                activity?.onBackPressed()
            }
        }

        binding.salwarKameez.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","category")
            bundle.putString("title","Salwar Kameez")
            findNavController().navigate(R.id.action_categoryFragment_to_shopingFragment,bundle)
        }

        binding.chuddidar.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","category")
            bundle.putString("title","Chuddidar")
            findNavController().navigate(R.id.action_categoryFragment_to_shopingFragment,bundle)
        }

        binding.legins.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","category")
            bundle.putString("title","Leggings")
            findNavController().navigate(R.id.action_categoryFragment_to_shopingFragment,bundle)
        }

        binding.kurti.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","category")
            bundle.putString("title","Kurti")
            findNavController().navigate(R.id.action_categoryFragment_to_shopingFragment,bundle)
        }

        binding.cigarDress.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","category")
            bundle.putString("title", "Cigar Pant Dress")
            findNavController().navigate(R.id.action_categoryFragment_to_shopingFragment,bundle)
        }

        binding.dressMaterial.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","category")
            bundle.putString("title", "Kurti")
            findNavController().navigate(R.id.action_categoryFragment_to_shopingFragment,bundle)
        }

        binding.onePiece.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","category")
            bundle.putString("title", "One Piece")
            findNavController().navigate(R.id.action_categoryFragment_to_shopingFragment,bundle)
        }






        return binding.root
    }


}