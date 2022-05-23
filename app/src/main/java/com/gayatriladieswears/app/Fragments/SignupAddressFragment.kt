package com.gayatriladieswears.app.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.Model.User
import com.gayatriladieswears.app.databinding.FragmentSignupAddressBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.firebase.auth.FirebaseAuth

class SignupAddressFragment : Fragment() {

    private lateinit var binding:FragmentSignupAddressBinding
    private lateinit var firstName:String
    private lateinit var lastName:String
    private lateinit var phone:String
    private lateinit var uid:String
    private lateinit var bundle:Bundle
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupAddressBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()

        firstName = arguments?.getString("firstName").toString()
        lastName = arguments?.getString("lastName").toString()
        phone = arguments?.getString("phone").toString()
        uid = arguments?.getString("uid").toString()






        binding.letsGoBtnSignup.setOnClickListener {

            if(binding.editTextAddressSignup.text.toString() == ""){
                binding.editTextAddressSignup.hint = "Please Enter Address"
                binding.editTextAddressSignup.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()
                removePhoneKeypad()
            }else{
                if(binding.editTextPincodeSignup.text.toString() == ""){
                    binding.editTextPincodeSignup.hint = "Please Enter Pincode"
                    binding.editTextPincodeSignup.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()
                    removePhoneKeypad()
                }else{
                    if(binding.editTextDistrictSignup.text.toString() == ""){
                        binding.editTextDistrictSignup.hint = "Please Enter District"
                        binding.editTextDistrictSignup.setHintTextColor(resources.getColor(R.color.red))
                        vibratePhone()
                        removePhoneKeypad()
                    }else{
                        if(binding.editTextStateSignup.text.toString() == ""){
                            binding.editTextStateSignup.hint = "Please Enter State"
                            binding.editTextStateSignup.setHintTextColor(resources.getColor(R.color.red))
                            vibratePhone()
                            removePhoneKeypad()

                        }else{
                            removePhoneKeypad()
                            val user = User(
                                uid,
                                phone.trim{it <= ' '},
                                firstName.trim{it <= ' '},
                                lastName.trim{it <= ' '},
                                binding.editTextAddressSignup.text.toString(),
                                binding.editTextPincodeSignup.text.toString().trim{it <= ' '},
                                binding.editTextDistrictSignup.text.toString().trim{it <= ' '},
                                binding.editTextStateSignup.text.toString().trim{it <= ' '}
                            )


                            binding.progressBarLinear.visibility = View.VISIBLE
                            binding.imageView6.visibility = View.INVISIBLE
                            FirestoreClass().register(this,user,binding.progressBarLinear)
                        }
                    }
                }
            }





        }

        return binding.root
    }

    fun removePhoneKeypad() {
        val inputManager: InputMethodManager = view
            ?.getContext()
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val binder = requireView().windowToken
        inputManager.hideSoftInputFromWindow(
            binder,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }










}