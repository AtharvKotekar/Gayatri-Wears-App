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
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class OrderAddressFragment : Fragment() {
    lateinit var dialog:Dialog
    private lateinit var binding: FragmentOrderAddressBinding
    var fromProfile:Boolean = false
    var selectedAddress = ArrayList<Address>()
    lateinit var adaptor:AddressAdaptor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOrderAddressBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(com.gayatriladieswears.app.R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()

        fromProfile = arguments?.getBoolean("fromProfile") == true

        if(fromProfile){
            binding.productNameTextTop.text = "Address"
            binding.bagBtn.visibility = View.GONE
            binding.textView28.visibility = View.GONE
            binding.textView36.visibility = View.GONE
            binding.nextBtn.visibility = View.GONE
        }




        FirestoreClass().getAddress(this,FirebaseAuth.getInstance().currentUser!!.uid)

        binding.addAddressBtn.setOnClickListener {
            findNavController().navigate(com.gayatriladieswears.app.R.id.action_orderAddressFragment_to_orderAddAdressFragment)

        }

        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == com.gayatriladieswears.app.R.id.orderAddressFragment) {
                activity?.onBackPressed()
            }
        }

        binding.bagBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == com.gayatriladieswears.app.R.id.orderAddressFragment) {
                activity?.onBackPressed()
            }
        }

        binding.nextBtn.setOnClickListener {
            if (selectedAddress.isEmpty()){
                val snackBar = Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Please select the Address for Delivery.",
                    Snackbar.LENGTH_LONG
                )
                snackBar.setBackgroundTint(resources.getColor(com.gayatriladieswears.app.R.color.red))
                snackBar.setTextColor(resources.getColor(com.gayatriladieswears.app.R.color.white))
                snackBar.show()
                vibratePhone()
            }else{
                for (i in selectedAddress){
                    val bundle = Bundle()
                    bundle.putString("name",i.fullName)
                    bundle.putString("pincode",i.pinCode)
                    bundle.putString("address",i.address)
                    bundle.putString("landmark",i.landMark)
                    bundle.putString("phone",i.phoneNumber)
                    bundle.putString("addressId",i.id)
                    bundle.putString("addressTag",i.tag)
                    bundle.putString("email",i.emailID)
                    findNavController().navigate(com.gayatriladieswears.app.R.id.action_orderAddressFragment_to_checkOutFragment,bundle)
                }

            }
        }



        return binding.root
    }

    fun getAddressList(addressList: ArrayList<Address>) {
        if (addressList.isEmpty()){
            binding.emptyAddressText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
            binding.nextBtn.visibility = View.GONE
            dialog.dismiss()
        }else{
            binding.emptyAddressText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            adaptor = AddressAdaptor(requireContext(),this@OrderAddressFragment,addressList,fromProfile)
            binding.recyclerView.adapter = adaptor
            binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            dialog.dismiss()
        }

    }


}