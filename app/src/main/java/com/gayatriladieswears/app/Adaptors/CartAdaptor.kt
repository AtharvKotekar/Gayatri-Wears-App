package com.gayatriladieswears.app.Adaptors

import android.app.Dialog
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Fragments.CartFragment
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.Model.Info
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CartAdaptor(private val context: Context,private var fragment: CartFragment,private var list: ArrayList<CartItem>) : RecyclerView.Adapter<CartAdaptor.myViewHolder>() {

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image = view.findViewById<ImageView>(R.id.cart_image)
        var name = view.findViewById<TextView>(R.id.cart_name)
        var dis = view.findViewById<TextView>(R.id.cart_des)
        var size = view.findViewById<TextView>(R.id.cart_size)
        var color = view.findViewById<TextView>(R.id.cart_color)
        var price = view.findViewById<TextView>(R.id.cart_price)
        var mrp = view.findViewById<TextView>(R.id.cart_mrp)
        var add = view.findViewById<ImageView>(R.id.cart_add)
        var sub = view.findViewById<ImageView>(R.id.cart_minus)
        var delete = view.findViewById<ImageView>(R.id.cart_delete)
        var quality = view.findViewById<TextView>(R.id.cart_quantity)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return CartAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cart_iteam, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]

        Glide
            .with(context)
            .load(model.image)
            .placeholder(R.drawable.baseline_shopping_bag_24)
            .centerCrop()
            .into(holder.image)

        holder.name.text = model.name
        holder.dis.text = model.dis
        holder.price.text = model.price.toString()
        holder.mrp.text = model.mrp.toString()
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        holder.size.text = model.size
        holder.color.text = model.color
        holder.quality.text = model.cartQuantity


        holder.image.setOnClickListener {
            fragment.dialog.show()
            FirestoreClass().getProductById(fragment,model.productId,holder)
        }

        var quantity:Int = model.cartQuantity.toInt()
        holder.add.setOnClickListener {
            quantity++
            FirestoreClass().updateCart(fragment,quantity.toString(),model.userId,model.productId)
            FirestoreClass().getCartProducts(fragment,model.userId)
            fragment.dialog.show()
        }
        holder.sub.setOnClickListener {
            if(quantity > 1){
                quantity--
                FirestoreClass().updateCart(fragment,quantity.toString(),model.userId,model.productId)
                FirestoreClass().getCartProducts(fragment,model.userId)
                fragment.dialog.show()
            }else{
                val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "You must have to select at least 1 quantity.", Snackbar.LENGTH_SHORT)
                snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
            }


        }


        holder.delete.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(context,R.style.AppCompatAlertDialogStyle)
                dialog.setTitle("Remove From Bag")
                dialog.setMessage("Do you really want to remove ${model.name} from Bag?")
                dialog.background = context.resources.getDrawable(R.drawable.loading_dialog_bg)
                dialog.setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                dialog.setPositiveButton("Remove") { dialog, which ->
                    FirestoreClass().removeCartProduct(fragment,model.productId,FirebaseAuth.getInstance().currentUser!!.uid)
                    FirestoreClass().getCartProducts(fragment,FirebaseAuth.getInstance().currentUser!!.uid)
                }
                dialog.show()
        }

    }

    override fun getItemCount(): Int {
        return  list.size
    }




}