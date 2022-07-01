package com.gayatriladieswears.app.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.User
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentTermsAndContionsBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar


class TermsAndContionsFragment : Fragment() {

    private lateinit var binding:FragmentTermsAndContionsBinding
    private lateinit var user:User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTermsAndContionsBinding.inflate(inflater,container,false)

        user = arguments?.getParcelable<User>("user") as User


        binding.agreeBtn.setOnClickListener {
            if(binding.checkBox.isChecked){
                FirestoreClass().register(this,user)
            }else{
                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Please Check Terms & Conditions.", Snackbar.LENGTH_LONG)
                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                snackBar.setTextColor(resources.getColor(R.color.white))
                snackBar.show()
                vibratePhone()
            }
        }
        return binding.root
    }


}