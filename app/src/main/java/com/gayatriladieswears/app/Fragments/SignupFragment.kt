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
        bundle = Bundle()



       binding.editTextPhonenumberSignup.setOnFocusChangeListener { view, hasChanged ->
           if(hasChanged){
               Selection.setSelection(binding.editTextPhonenumberSignup.getText(), binding.editTextPhonenumberSignup.getText().length)
               binding.editTextPhonenumberSignup.addTextChangedListener(object : TextWatcher {
                   override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                       // TODO Auto-generated method stub
                   }

                   override fun beforeTextChanged(
                       s: CharSequence, start: Int, count: Int,
                       after: Int
                   ) {
                       // TODO Auto-generated method stub
                   }

                   override fun afterTextChanged(s: Editable) {
                       if (!s.toString().startsWith("+91")) {
                           binding.editTextPhonenumberSignup.setText("+91")
                           Selection.setSelection(binding.editTextPhonenumberSignup.getText(), binding.editTextPhonenumberSignup.getText().length)
                       }
                   }
               })
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



                        var docRef = FirestoreClass().mFirestore.collection("users").document(binding.editTextPhonenumberSignup.text.toString());

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
                                        removePhoneKeypad()
                                    } else {
                                        login(binding.editTextPhonenumberSignup.text.toString())
                                        removePhoneKeypad()
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






fun login(phoneNumber: String) {
    var number = phoneNumber.trim()
    binding.progressBarLinear.visibility = View.VISIBLE
    binding.imageView5.visibility = View.INVISIBLE
    if (!number.isEmpty()) {
        Log.i(ContentValues.TAG, "login: Getting otp")
        sendVerificationcode(number)
    } else {
        Toast.makeText(context, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show()
    }
}

fun sendVerificationcode(number: String) {
    Log.i(ContentValues.TAG, "sendVerificationcode: got it")
    options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(number) // Phone number to verify
        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
        .setActivity(LoginActivity()) // Activity (for callback binding)
        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
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