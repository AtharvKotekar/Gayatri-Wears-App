package com.gayatriladieswears.app.Fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.Activities.LoginActivity
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding
    lateinit var dialog1:Dialog
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        dialog1 = Dialog(requireContext())
        dialog1.setContentView(com.gayatriladieswears.app.R.layout.laoding_dialog)
        dialog1.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog1.setCancelable(false)
        dialog1.show()

        FirestoreClass().mFirestore.collection("users").whereEqualTo("id",FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                for (i in it.documents){
                    binding.profileNameText.text = i.getString("firstName") + " " + i.getString("lastName")
                    binding.profilePhoneText.text = i.getString("phone")
                    dialog1.dismiss()
                }
            }

        binding.profileMyBag.setOnClickListener {
            findNavController().navigate(com.gayatriladieswears.app.R.id.action_profileFragment_to_cartFragment)
        }

        binding.profileMyOrders.setOnClickListener {
            findNavController().navigate(com.gayatriladieswears.app.R.id.action_profileFragment_to_orderFragment)
        }

        binding.profileAddresses.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("fromProfile",true)
            findNavController().navigate(com.gayatriladieswears.app.R.id.action_profileFragment_to_orderAddressFragment,bundle)
        }

        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.profileFragment) {
                activity?.onBackPressed()
            }
        }

        binding.logoutBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("Logout")
            dialog.setMessage("Do you really want to Logout?")
            dialog.background = context?.resources?.getDrawable(R.drawable.black_btn_bg)
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                requireActivity().run {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                FirebaseAuth.getInstance().signOut()
            }
            dialog.show()
        }

        return binding.root
    }


}