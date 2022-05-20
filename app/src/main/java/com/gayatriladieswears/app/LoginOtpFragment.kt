package com.gayatriladieswears.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.databinding.FragmentLoginOtpBinding

class LoginOtpFragment : Fragment() {

    private lateinit var binding:FragmentLoginOtpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginOtpBinding.inflate(inflater,container,false)

        binding.submitBnt.setOnClickListener {
            findNavController().navigate(R.id.action_loginOtpFragment_to_loginPasswardFragment)
        }

        return binding.root
    }

}