package com.gayatriladieswears.app.Adaptors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Fragments.CartFragment
import com.gayatriladieswears.app.Fragments.OrderAddressFragment
import com.gayatriladieswears.app.Model.Address
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class AddressAdaptor(private val context: Context, private var fragment: OrderAddressFragment, private var list: ArrayList<Address>) : RecyclerView.Adapter<AddressAdaptor.myViewHolder>(){
    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var name = view.findViewById<TextView>(R.id.address_name)
        var address = view.findViewById<TextView>(R.id.address_address_text)
        var phone = view.findViewById<TextView>(R.id.address_phone_text)
        var tag = view.findViewById<TextView>(R.id.address_tag_text)
        var delet = view.findViewById<Button>(R.id.address_remove_btn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return AddressAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.address_iteam, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]
        val bundle = Bundle()
        holder.name.text = model.fullName.toString()
        holder.address.text = model.address.toString()
        holder.tag.text = model.tag.toString()
        holder.phone.text = model.phoneNumber
        holder.delet.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(context,R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("Remove Address")
            dialog.setMessage("Do you really want to remove this Address?")
            dialog.background = context.resources.getDrawable(R.drawable.black_btn_bg)
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
                }
            dialog.setPositiveButton("Remove") { dialog, which ->
                fragment.dialog.show()
                FirestoreClass().deleteAddress(fragment,FirebaseAuth.getInstance().currentUser!!.uid,model.id)
                FirestoreClass().getAddress(fragment,FirebaseAuth.getInstance().currentUser!!.uid)
                }
            dialog.show()
            }

        holder.itemView.setOnClickListener {
            if (fragment.fromProfile){

            }else{
                bundle.putString("name",model.fullName)
                bundle.putString("pincode",model.pinCode)
                bundle.putString("address",model.address)
                bundle.putString("landmark",model.landMark)
                bundle.putString("phone",model.phoneNumber)
                bundle.putString("addressId",model.id)
                bundle.putString("addressTag",model.tag)
                holder.itemView.findNavController().navigate(R.id.action_orderAddressFragment_to_checkOutFragment,bundle)
            }
            }


        }


    override fun getItemCount(): Int {
        return list.size
    }
}