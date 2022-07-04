package com.gayatriladieswears.app.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.Activities.LoginActivity
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.User
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentSignupOtpBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*


class SignupOtpFragment : Fragment() {

    private lateinit var binding:FragmentSignupOtpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId:String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firstName:String
    private lateinit var lastName:String
    private lateinit var phone:String
    private lateinit var uid:String
    private lateinit var bundle:Bundle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupOtpBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        bundle = Bundle()

        storedVerificationId = arguments?.getString("id").toString()
        firstName = arguments?.getString("firstName").toString()
        lastName = arguments?.getString("lastName").toString()
        phone = arguments?.getString("phone").toString()


        binding.submitBtnSignup.setOnClickListener {
            if(binding.editTextOtpSignup.text.toString() == ""){
                binding.editTextOtpSignup.hint = "Please Enter OTP"
                binding.editTextOtpSignup.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()
                removePhoneKeypad()
            }
            else {
                removePhoneKeypad()
                verify()
            }
        }

        return binding.root
    }

    fun removePhoneKeypad() {
        val inputManager: InputMethodManager = view
            ?.context
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val binder = requireView().windowToken
        inputManager.hideSoftInputFromWindow(
            binder,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    fun verify(){
        binding.imageView4.visibility = View.INVISIBLE
        binding.progressBarLinear.visibility = View.VISIBLE


        val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
            storedVerificationId, binding.editTextOtpSignup.text.toString())
        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(LoginActivity()) { task ->
                if (task.isSuccessful) {
                    firebaseUser = task.result!!.user!!
                    uid = firebaseUser.uid

                    removePhoneKeypad()
                    val user = User(
                        uid,
                        phone.trim{it <= ' '},
                        firstName.trim{it <= ' '},
                        lastName.trim{it <= ' '},
                    )

                    val bundle = Bundle()
                    bundle.putParcelable("user",user)

                    findNavController().navigate(R.id.action_signupOtpFragment_to_termsAndContionsFragment,bundle)
                    binding.imageView4.visibility = View.VISIBLE
                    binding.progressBarLinear.visibility = View.GONE




                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                        binding.imageView4.visibility = View.VISIBLE
                        binding.progressBarLinear.visibility = View.GONE
                        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Invalid OTP", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(resources.getColor(R.color.red))
                        snackBar.setTextColor(resources.getColor(R.color.white))
                        snackBar.show()
                        vibratePhone()
                    }
                }
            }
    }


}