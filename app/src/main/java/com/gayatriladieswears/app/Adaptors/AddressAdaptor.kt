package com.gayatriladieswears.app.Adaptors

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Fragments.CartFragment
import com.gayatriladieswears.app.Fragments.OrderAddressFragment
import com.gayatriladieswears.app.Fragments.OrderReturnFragment
import com.gayatriladieswears.app.Model.Address
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class AddressAdaptor(private val context: Context, private var fragment: OrderAddressFragment, private var list: ArrayList<Address>,private var fromProfile:Boolean) : RecyclerView.Adapter<AddressAdaptor.myViewHolder>(){
    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var name = view.findViewById<TextView>(R.id.address_name)
        var address = view.findViewById<TextView>(R.id.address_address_text)
        var phone = view.findViewById<TextView>(R.id.address_phone_text)
        var tag = view.findViewById<TextView>(R.id.address_tag_text)
        var delet = view.findViewById<Button>(R.id.address_remove_btn)
        var checkbox = view.findViewById<CheckBox>(R.id.checkBox)
        var cardView = view.findViewById<CardView>(R.id.bg_card)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return AddressAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.address_iteam, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]
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

        if (fromProfile){
            holder.cardView.alpha = 1f
            holder.checkbox.visibility = View.GONE
            holder.name.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(24,64,0,0)
            }

        }else{
            if(holder.checkbox.isChecked){
                holder.cardView.alpha = 1f
            }else{
                holder.cardView.alpha = 0.4f
            }

            if(fragment.selectedAddress.contains(model)){
                holder.checkbox.isChecked = true
                holder.cardView.alpha = 1f
            }else{
                holder.checkbox.isChecked = false
                holder.cardView.alpha = 0.4f
            }


            holder.checkbox.visibility = View.VISIBLE
        }





        holder.checkbox.setOnClickListener {
            if(holder.checkbox.isChecked){
                for(i in list){
                    if(i != model){
                        (fragment as OrderAddressFragment).selectedAddress.remove(i)
                        fragment.adaptor.notifyDataSetChanged()
                    }else {
                        holder.checkbox.isChecked = true
                        holder.cardView.alpha = 1F
                        (fragment as OrderAddressFragment).selectedAddress.clear()
                        (fragment as OrderAddressFragment).selectedAddress.add(model)
                        Log.e(ContentValues.TAG, "onBindViewHolder: ${(fragment as OrderAddressFragment).selectedAddress.size}")
                    }
                }
            }else{
                holder.checkbox.isChecked = false
                holder.cardView.alpha = 0.4F
                (fragment as OrderAddressFragment).selectedAddress.clear()
                (fragment as OrderAddressFragment).selectedAddress.remove(model)
                fragment.adaptor.notifyDataSetChanged()
                Log.e(ContentValues.TAG, "onBindViewHolder: ${(fragment as OrderAddressFragment).selectedAddress.size}")
            }
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }
}