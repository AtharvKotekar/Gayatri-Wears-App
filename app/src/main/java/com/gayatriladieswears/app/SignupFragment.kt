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
            if(binding.editTextFullNameSignup.text.toString() == ""){
                binding.editTextFullNameSignup.hint = "Please Enter Your Fullname"
                binding.editTextFullNameSignup.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()
            }else{
                if (binding.editTextPhonenumberSignup.text.toString() == ""){
                    binding.editTextPhonenumberSignup.hint = "Please Enter Your Phonenumber"
                    binding.editTextPhonenumberSignup.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()
                }else{
                    if(binding.editTextCreatePasswardSignup.text.toString() == ""){
                        binding.editTextCreatePasswardSignup.hint = "Please Create A Password"
                        binding.editTextCreatePasswardSignup.setHintTextColor(resources.getColor(R.color.red))
                        vibratePhone()
                    }else{
                        if(binding.editTextConfirmPasswardSignup.text.toString() == ""){
                            binding.editTextConfirmPasswardSignup.hint = "Please Confirm Your Password"
                            binding.editTextConfirmPasswardSignup.setHintTextColor(resources.getColor(R.color.red))
                            vibratePhone()
                        }else{
                            findNavController().navigate(R.id.action_signupFragment_to_signupOtpFragment)
                        }
                    }
                }
            }







        }

        return binding.root
    }


}