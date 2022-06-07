package com.gayatriladieswears.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.Address
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentOrderAddAdressBinding
import com.gayatriladieswears.app.databinding.FragmentOrderAddressBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class OrderAddAdressFragment : Fragment() {

    private lateinit var binding: FragmentOrderAddAdressBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderAddAdressBinding.inflate(inflater,container,false)

        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.orderAddAdressFragment) {
                activity?.onBackPressed()
            }
        }

        binding.addThisAddressBtn.setOnClickListener {
            if(binding.fullName.text.toString() == ""){
                binding.fullName.hint = "Please enter your Name"
                binding.fullName.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()
            }else{
                if(binding.phoneNumber.text.toString() == ""){
                    binding.phoneNumber.hint = "Please enter your Phonenumber"
                    binding.phoneNumber.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()
                }else{
                    if(binding.pincode.text.toString() == ""){
                        binding.pincode.hint = "Please enter Pincode"
                        binding.pincode.setHintTextColor(resources.getColor(R.color.red))
                        vibratePhone()
                    }else{
                        if(binding.address.text.toString() == ""){
                            binding.address.hint = "Please enter your Address"
                            binding.address.setHintTextColor(resources.getColor(R.color.red))
                            vibratePhone()
                        }else{
                            if(binding.landMark.text.toString() == ""){
                                binding.landMark.hint = "Please enter nearest Landmark"
                                binding.landMark.setHintTextColor(resources.getColor(R.color.red))
                                vibratePhone()
                            }else{

                                val tag = binding.radioGroup.findViewById<RadioButton>(binding.radioGroup.checkedRadioButtonId).text.toString()

                                val addressModel = Address(
                                    binding.fullName.text.toString(),binding.phoneNumber.text.toString(),
                                    binding.pincode.text.toString(),binding.address.text.toString(),binding.landMark.text.toString(),tag,FirebaseAuth.getInstance().currentUser!!.uid.toString())
                                FirestoreClass().addAddress(this,addressModel)
                                findNavController().navigate(R.id.action_orderAddAdressFragment_to_orderAddressFragment)

                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }


    fun addAddress(){
        findNavController().navigate(R.id.action_orderAddAdressFragment_to_orderAddressFragment)
    }

}