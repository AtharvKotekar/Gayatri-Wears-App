package com.gayatriladieswears.app.Fragments

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.Activities.LoginActivity
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentSignupBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class SignupFragment : Fragment() {

    private lateinit var binding:FragmentSignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var storedVerificationId:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var options: PhoneAuthOptions
    private lateinit var bundle:Bundle



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(false)
        bundle = Bundle()

        binding.editTextPhonenumberSignup.isFocusableInTouchMode = true
        binding.editTextPhonenumberSignup.requestFocus()


        binding.editTextPhonenumberSignup.setOnFocusChangeListener { view, b ->
            if(b){
                binding.editTextPhonenumberSignup.hint = ""
            }else{
                binding.editTextPhonenumberSignup.hint = resources.getString(R.string.phonenumber_hint)
            }
        }



        binding.loginText.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginPhoneNumberFragment)
        }

        binding.getOtpBtnSignup.setOnClickListener {
            if(binding.editTextFirstNameSignup.text.toString() == ""){
                binding.editTextFirstNameSignup.hint = "Please Enter Your First Name"
                binding.editTextFirstNameSignup.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()
            }else{
                if(binding.editTextLastNameSignup.text.toString() == ""){
                    binding.editTextLastNameSignup.hint = "Please Enter Your Last Name"
                    binding.editTextLastNameSignup.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()
                }else{
                    if (binding.editTextPhonenumberSignup.text.toString() == ""){
                    binding.editTextPhonenumberSignup.hint = "Please Enter Your Phonenumber"
                    binding.editTextPhonenumberSignup.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()
                    }else{



                        val docRef = FirestoreClass().mFirestore.collection("users").document("+91${binding.editTextPhonenumberSignup.text.toString()}");


                        docRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if(document != null) {
                                    if (document.exists()) {
                                        removePhoneKeypad()
                                        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "User Already Exist, Please Try To Login", Snackbar.LENGTH_LONG)
                                        snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                        snackBar.setTextColor(resources.getColor(R.color.white))
                                        snackBar.show()
                                        vibratePhone()
                                    } else {
                                        removePhoneKeypad()
                                        login(binding.editTextPhonenumberSignup.text.toString())
                                    }
                                }
                            } else {

                                Toast.makeText(activity, "${task.exception}", Toast.LENGTH_LONG).show()
                            }
                        }




                        }


                    }

            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.i(ContentValues.TAG, "onVerificationCompleted: Getting Done")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.i(ContentValues.TAG, "onVerificationFailed: Oppps")
                Toast.makeText(context, "Verification Failed TRy Again PLease", Toast.LENGTH_LONG).show()
                binding.progressBarLinear.visibility = View.GONE
                binding.imageView5.visibility = View.VISIBLE
            }

            override  fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                storedVerificationId = verificationId
                Log.i(ContentValues.TAG, "onCodeSent: $verificationId")
                Log.d("TAG","onCodeSent:$verificationId")
                resendToken = token
                bundle.putString("id", storedVerificationId)
                bundle.putString("firstName", binding.editTextFirstNameSignup.text.toString())
                bundle.putString("lastName", binding.editTextLastNameSignup.text.toString())
                bundle.putString("phone", binding.editTextPhonenumberSignup.text.toString())
                binding.progressBarLinear.visibility = View.GONE
                findNavController().navigate(R.id.action_signupFragment_to_signupOtpFragment,bundle)

            }
        }

        return binding.root
    }






private fun login(phoneNumber: String) {
    val number = "+91${phoneNumber.trim()}"
    binding.progressBarLinear.visibility = View.VISIBLE
    binding.imageView5.visibility = View.INVISIBLE
    if (number.isNotEmpty()) {
        Log.i(ContentValues.TAG, "login: Getting otp")
        sendVerificationcode(number)
    } else {
        Toast.makeText(context, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show()
    }
}

private fun sendVerificationcode(number: String) {
    Log.i(ContentValues.TAG, "sendVerificationcode: got it")
    options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(number) // Phone number to verify
        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
        .setActivity(requireActivity()) // Activity (for callback binding)
        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}

    private fun removePhoneKeypad() {
        val inputManager: InputMethodManager = view
            ?.context
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val binder = requireView().windowToken
        inputManager.hideSoftInputFromWindow(
            binder,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }




}