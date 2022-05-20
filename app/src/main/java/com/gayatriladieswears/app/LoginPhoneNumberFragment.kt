package com.gayatriladieswears.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.databinding.FragmentLoginPhoneNumberBinding


class LoginPhoneNumberFragment : Fragment() {

    private lateinit var binding:FragmentLoginPhoneNumberBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginPhoneNumberBinding.inflate(inflater,container,false)

        binding.getOtpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginPhoneNumberFragment_to_loginOtpFragment)
        }

        binding.signupText.setOnClickListener {
            findNavController().navigate(R.id.action_loginPhoneNumberFragment_to_signupFragment)
        }

        return binding.root
    }

}