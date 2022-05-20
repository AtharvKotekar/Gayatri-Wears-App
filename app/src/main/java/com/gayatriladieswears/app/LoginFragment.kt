package com.gayatriladieswears.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.databinding.FragmentLoginBinding


class Login : Fragment() {

    private lateinit var binding:FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentLoginBinding.inflate(inflater,container,false)

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_loginPhoneNumberFragment)
        }

        binding.signupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signupFragment)
        }

        return binding.root
    }


}