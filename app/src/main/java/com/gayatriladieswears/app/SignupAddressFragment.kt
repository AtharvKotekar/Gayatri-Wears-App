package com.gayatriladieswears.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gayatriladieswears.app.databinding.FragmentSignupAddressBinding

class SignupAddressFragment : Fragment() {

    private lateinit var binding:FragmentSignupAddressBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupAddressBinding.inflate(inflater,container,false)

        binding.letsGoBtnSignup.setOnClickListener {

            if(binding.editTextAddressSignup.text.toString() == ""){
                binding.editTextAddressSignup.hint = "Please Enter Address"
                binding.editTextAddressSignup.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()
            }else{
                if(binding.editTextPincodeSignup.text.toString() == ""){
                    binding.editTextPincodeSignup.hint = "Please Enter Pincode"
                    binding.editTextPincodeSignup.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()
                }else{
                    if(binding.editTextDistrictSignup.text.toString() == ""){
                        binding.editTextDistrictSignup.hint = "Please Enter District"
                        binding.editTextDistrictSignup.setHintTextColor(resources.getColor(R.color.red))
                        vibratePhone()
                    }else{
                        if(binding.editTextStateSignup.text.toString() == ""){
                            binding.editTextStateSignup.hint = "Please Enter State"
                            binding.editTextStateSignup.setHintTextColor(resources.getColor(R.color.red))
                            vibratePhone()

                        }else{
                            requireActivity().run{
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }





        }

        return binding.root
    }


}