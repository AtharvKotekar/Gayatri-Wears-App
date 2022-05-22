package com.gayatriladieswears.app

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.databinding.FragmentLoginPhoneNumberBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class LoginPhoneNumberFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginPhoneNumberBinding
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var storedVerificationId:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var options: PhoneAuthOptions
    private lateinit var bundle:Bundle


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginPhoneNumberBinding.inflate(inflater, container, false)


        auth = FirebaseAuth.getInstance()
        bundle = Bundle()




        binding.getOtpBtn.setOnClickListener {
            if (binding.editTextPhoneNumberLogin.text.toString() == "") {
                binding.editTextPhoneNumberLogin.hint = "Please Enter Your Phonenumber"
                binding.editTextPhoneNumberLogin.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()


            } else {
                login(binding.editTextPhoneNumberLogin.text.toString())


            }
        }

        binding.signupText.setOnClickListener {
            findNavController().navigate(R.id.action_loginPhoneNumberFragment_to_signupFragment)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.i(TAG, "onVerificationCompleted: Getting Done")
                requireActivity().run {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.i(TAG, "onVerificationFailed: Oppps")
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.GONE
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {


                storedVerificationId = verificationId
                Log.i(TAG, "onCodeSent: $verificationId")
                Log.d("TAG","onCodeSent:$verificationId")
                resendToken = token
                bundle.putString("id", storedVerificationId)
                findNavController().navigate(R.id.action_loginPhoneNumberFragment_to_loginOtpFragment,bundle)
                binding.progressBar.visibility = View.GONE

            }
        }



        return binding.root

    }


    fun login(phoneNumber: String) {
        var number = phoneNumber.trim()
        binding.progressBar.visibility = View.VISIBLE
        if (!number.isEmpty()) {
            number = "+91$number"
            Log.i(TAG, "login: Getting otp")
            sendVerificationcode(number)
        } else {
            Toast.makeText(context, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendVerificationcode(number: String) {
        Log.i(TAG, "sendVerificationcode: got it")
        options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(LoginActivity()) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}