package com.gayatriladieswears.app.Fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.Adaptors.OrderAdaptor
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.Order
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentOrderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class OrderFragment : Fragment() {
    lateinit var dialog:Dialog
    lateinit var adaptor:RecyclerView.Adapter<OrderAdaptor.myViewHolder>
    private lateinit var binding:FragmentOrderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()

        CoroutineScope(IO).launch {
            FirestoreClass().getOrderedProducts(this@OrderFragment)
        }



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
            adaptor = context?.let { OrderAdaptor(it,this,iteamList) }!!
            binding.recyclerViewOrder.adapter = adaptor
            binding.recyclerViewOrder.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }
}