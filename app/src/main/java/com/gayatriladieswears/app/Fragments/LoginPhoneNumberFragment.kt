package com.gayatriladieswears.app.Fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.Activities.LoginActivity
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentLoginPhoneNumberBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class LoginPhoneNumberFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginPhoneNumberBinding
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var storedVerificationId:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var options: PhoneAuthOptions
    private lateinit var bundle:Bundle



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginPhoneNumberBinding.inflate(inflater, container, false)


        auth = FirebaseAuth.getInstance()
        bundle = Bundle()


        binding.editTextPhoneNumberLogin.setOnFocusChangeListener { _, hasChanged ->
            if(hasChanged){
                Selection.setSelection(binding.editTextPhoneNumberLogin.text, binding.editTextPhoneNumberLogin.text.length)


                binding.editTextPhoneNumberLogin.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    }

                    override fun beforeTextChanged(
                        s: CharSequence, start: Int, count: Int,
                        after: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        if (!s.toString().startsWith("+91")) {
                            binding.editTextPhoneNumberLogin.setText("+91")
                            Selection.setSelection(binding.editTextPhoneNumberLogin.text, binding.editTextPhoneNumberLogin.text.length)
                        }
                    }
                })
            }
        }





        binding.getOtpBtn.setOnClickListener {
            if (binding.editTextPhoneNumberLogin.text.toString() == "") {
                binding.editTextPhoneNumberLogin.hint = "Please Enter Your Phonenumber"
                binding.editTextPhoneNumberLogin.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()


            } else {

                val docRef = FirestoreClass().mFirestore.collection("users").document(binding.editTextPhoneNumberLogin.text.toString())

                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if(document != null) {
                            if (document.exists()) {
                                removePhoneKeypad()
                                login(binding.editTextPhoneNumberLogin.text.toString())
                            } else {
                                removePhoneKeypad()
                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "User Not Found, Please Try To Signup", Snackbar.LENGTH_LONG)
                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                snackBar.setTextColor(resources.getColor(R.color.white))
                                snackBar.show()
                                vibratePhone()

                            }
                        }
                    } else {

                        Toast.makeText(activity, "${task.exception}", Toast.LENGTH_LONG).show()
                    }
                }



            }
        }

        binding.signupText.setOnClickListener {
            findNavController().navigate(R.id.action_loginPhoneNumberFragment_to_signupFragment)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.i(TAG, "onVerificationCompleted: Getting Done")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.i(TAG, "onVerificationFailed: Oppps")
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                binding.progressBarLinear.visibility = View.GONE
                binding.imageView5.visibility = View.VISIBLE
            }

            override  fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                storedVerificationId = verificationId
                Log.i(TAG, "onCodeSent: $verificationId")
                Log.d("TAG","onCodeSent:$verificationId")
                resendToken = token
                bundle.putString("id", storedVerificationId)
                binding.progressBarLinear.visibility = View.GONE
                findNavController().navigate(R.id.action_loginPhoneNumberFragment_to_loginOtpFragment,bundle)

            }
        }



        return binding.root

    }


    private fun login(phoneNumber: String) {
        val number = phoneNumber.trim()
        binding.imageView5.visibility = View.INVISIBLE
        binding.progressBarLinear.visibility = View.VISIBLE
        if (!number.isEmpty()) {
            Log.i(TAG, "login: Getting otp")
            sendVerificationcode(number)
        } else {
            Toast.makeText(context, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        Log.i(TAG, "sendVerificationcode: got it")
        options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(LoginActivity()) // Activity (for callback binding)
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