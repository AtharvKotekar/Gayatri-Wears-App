package com.gayatriladieswears.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentFullScreenBinding

class FullScreenFragment : Fragment() {
   private lateinit var binding:FragmentFullScreenBinding
   lateinit var image:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullScreenBinding.inflate(inflater,container,false)

        image = arguments?.getString("image").toString()

        Glide
            .with(requireContext())
            .load(image)
            .placeholder(R.drawable.baseline_shopping_bag_24)
            .centerCrop()
            .into(binding.imageView12)


        binding.closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }


}