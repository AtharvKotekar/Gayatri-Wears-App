package com.gayatriladieswears.app.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentLoginOtpBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.Executor

class LoginOtpFragment : Fragment() {

    private lateinit var binding: FragmentLoginOtpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginOtpBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        storedVerificationId = arguments?.getString("id").toString()


            binding.submitBnt.setOnClickListener {
                if (binding.editTextOtpLogin.text.toString() == "") {
                    binding.editTextOtpLogin.hint = "Please enter OTP"
                    binding.editTextOtpLogin.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()
                    removePhoneKeypad()

                } else {
                    verify()
                    removePhoneKeypad()
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
                storedVerificationId, binding.editTextOtpLogin.text.toString())
            signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.progressBarLinear.visibility = View.GONE

                        requireActivity().run {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.progressBarLinear.visibility = View.GONE
                        binding.imageView4.visibility = View.VISIBLE
                        removePhoneKeypad()
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
