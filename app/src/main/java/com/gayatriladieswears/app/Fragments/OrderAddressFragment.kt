package com.gayatriladieswears.app.Fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gayatriladieswears.app.Adaptors.AddressAdaptor
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.Address
import com.gayatriladieswears.app.databinding.FragmentOrderAddressBinding
import com.google.firebase.auth.FirebaseAuth

class OrderAddressFragment : Fragment() {
    lateinit var dialog:Dialog
    private lateinit var binding: FragmentOrderAddressBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOrderAddressBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(com.gayatriladieswears.app.R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()


        FirestoreClass().getAddress(this,FirebaseAuth.getInstance().currentUser!!.uid)

        binding.addAddressBtn.setOnClickListener {
            findNavController().navigate(com.gayatriladieswears.app.R.id.action_orderAddressFragment_to_orderAddAdressFragment)

        }

        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == com.gayatriladieswears.app.R.id.orderAddressFragment) {
                activity?.onBackPressed()
            }
        }



        return binding.root
    }

    fun getAddressList(addressList: ArrayList<Address>) {
        val adaptor = AddressAdaptor(requireContext(),this@OrderAddressFragment,addressList)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        dialog.dismiss()
    }


}