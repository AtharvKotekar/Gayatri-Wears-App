package com.gayatriladieswears.app

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gayatriladieswears.app.Adaptors.AddressAdaptor
import com.gayatriladieswears.app.Adaptors.OrderAdaptor
import com.gayatriladieswears.app.Model.Order
import com.gayatriladieswears.app.databinding.FragmentOrderBinding


class OrderFragment : Fragment() {
    lateinit var dialog:Dialog
    private lateinit var binding:FragmentOrderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(com.gayatriladieswears.app.R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()

        FirestoreClass().getOrderedProducts(this)


        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.orderFragment) {
                activity?.onBackPressed()
            }
        }

        binding.shopBtn.setOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_homeFragment)
        }

        return binding.root
    }

    fun getProducts(iteamList:ArrayList<Order>){
        if(iteamList.isEmpty()){
            binding.emtyCartImage.visibility = View.VISIBLE
            binding.emptyCartText.visibility = View.VISIBLE
            binding.shopBtn.visibility = View.VISIBLE
            binding.recyclerViewOrder.visibility = View.GONE
            dialog.dismiss()
        }else{
            binding.emtyCartImage.visibility = View.GONE
            binding.emptyCartText.visibility = View.GONE
            binding.shopBtn.visibility = View.GONE
            binding.recyclerViewOrder.visibility = View.VISIBLE
            val adaptor = OrderAdaptor(requireContext(),this,iteamList)
            binding.recyclerViewOrder.adapter = adaptor
            binding.recyclerViewOrder.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            dialog.dismiss()
        }

    }

}