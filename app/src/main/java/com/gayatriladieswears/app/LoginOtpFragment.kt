package com.gayatriladieswears.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.databinding.FragmentLoginOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class LoginOtpFragment : Fragment() {

    private lateinit var binding: FragmentLoginOtpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId:String
    private lateinit var otpGiven: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): ConstraintLayout {
        binding = FragmentLoginOtpBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        storedVerificationId = arguments?.getString("id").toString()
        otpGiven = binding.editTextOtpLogin.text.toString()



            if (currentUser == null) {
                requireActivity().run {
                    requireActivity().run {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }




            binding.submitBnt.setOnClickListener {
                if (binding.editTextOtpLogin.text.toString() == "") {
                    binding.editTextOtpLogin.hint = "Please enter OTP"
                    binding.editTextOtpLogin.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()

                } else {
                    verify(otpGiven)
                }
            }

            return binding.root

    }

    fun verify(otpGiven:String){
        var otp=otpGiven.trim()
        if(!otp.isEmpty()){
            Toast.makeText(context, "Getting Otp", Toast.LENGTH_SHORT).show()
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                storedVerificationId, otp)
            signInWithPhoneAuthCredential(credential)
        }else{
            Toast.makeText(context,"Enter OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Toast.makeText(context, "checking", Toast.LENGTH_SHORT).show()
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(LoginActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()

                        requireActivity().run {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

// ...
                } else {
                    Toast.makeText(context, "Fuck", Toast.LENGTH_SHORT).show()

// Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid
                        Toast.makeText(context,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}
