package com.gayatriladieswears.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.databinding.FragmentSignupBinding


class SignupFragment : Fragment() {

    private lateinit var binding:FragmentSignupBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupBinding.inflate(inflater,container,false)

        binding.loginText.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginPhoneNumberFragment)
        }

        binding.getOtpBtnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_signupOtpFragment)
        }

        return binding.root
    }


}