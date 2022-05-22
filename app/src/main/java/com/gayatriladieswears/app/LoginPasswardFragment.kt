package com.gayatriladieswears.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gayatriladieswears.app.databinding.FragmentLoginPasswardBinding


class LoginPasswardFragment : Fragment() {

    private lateinit var binding:FragmentLoginPasswardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginPasswardBinding.inflate(inflater,container,false)



        binding.letsGoBtn.setOnClickListener {
            if(binding.editTextPasswordLogin.text.toString() == ""){
                binding.editTextPasswordLogin.hint = "Please Enter Your Password"
                binding.editTextPasswordLogin.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()
            }else {
                requireActivity().run {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        return binding.root
    }

}