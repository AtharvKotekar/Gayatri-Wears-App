package com.gayatriladieswears.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.databinding.FragmentSignupOtpBinding


class SignupOtpFragment : Fragment() {

    private lateinit var binding:FragmentSignupOtpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupOtpBinding.inflate(inflater,container,false)

        binding.submitBtnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_signupOtpFragment_to_signupAddressFragment)
        }

        return binding.root
    }


}