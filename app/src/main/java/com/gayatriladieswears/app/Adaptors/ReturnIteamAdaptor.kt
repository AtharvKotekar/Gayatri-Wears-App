package com.gayatriladieswears.app.Adaptors

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.Fragments.OrderReturnFragment
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar

class ReturnIteamAdaptor(private val context: Context, private var fragment: Fragment, private var list: ArrayList<CartItem>) : RecyclerView.Adapter<ReturnIteamAdaptor.myViewHolder>()  {



    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById<ImageView>(R.id.cart_image)
        var name: TextView = view.findViewById<TextView>(R.id.cart_name)
        var dis: TextView = view.findViewById<TextView>(R.id.cart_des)
        var size: TextView = view.findViewById<TextView>(R.id.cart_size)
        var color: TextView = view.findViewById<TextView>(R.id.cart_color)
        var price: TextView = view.findViewById<TextView>(R.id.cart_price)
        var mrp: TextView = view.findViewById<TextView>(R.id.cart_mrp)
        var add: ImageView = view.findViewById<ImageView>(R.id.cart_add)
        var sub: ImageView = view.findViewById<ImageView>(R.id.cart_minus)
        var quality: TextView = view.findViewById<TextView>(R.id.cart_quantity)
        var checkBtn: CheckBox = view.findViewById<CheckBox>(R.id.return_radio_btn)
        var cardView: CardView = view.findViewById<CardView>(R.id.main_card)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return ReturnIteamAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.order_return_item, parent, false)
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



        when(fragment){
            is OrderReturnFragment -> {

                if(holder.checkBtn.isChecked){
                    holder.checkBtn.isChecked = true
                    holder.cardView.alpha = 1F
                    (fragment as OrderReturnFragment).selectedProducts.add(model)
                    Log.e(TAG, "onBindViewHolder: ${(fragment as OrderReturnFragment).selectedProducts.size}")
                }else{

                    holder.checkBtn.isChecked = false
                    holder.cardView.alpha = 0.4F
                    (fragment as OrderReturnFragment).selectedProducts.remove(model)

                    (fragment as OrderReturnFragment).selectedProducts.remove(model)
                    Log.e(TAG, "onBindViewHolder: ${(fragment as OrderReturnFragment).selectedProducts.size}")
                }


                holder.checkBtn.setOnClickListener {
                    if(holder.checkBtn.isChecked){
                        holder.checkBtn.isChecked = true
                        holder.cardView.alpha = 1F
                        (fragment as OrderReturnFragment).selectedProducts.add(model)
                        Log.e(TAG, "onBindViewHolder: ${(fragment as OrderReturnFragment).selectedProducts.size}")
                    }else{

                        holder.checkBtn.isChecked = false
                        holder.cardView.alpha = 0.4F
                        (fragment as OrderReturnFragment).selectedProducts.remove(model)

                        (fragment as OrderReturnFragment).selectedProducts.remove(model)
                        Log.e(TAG, "onBindViewHolder: ${(fragment as OrderReturnFragment).selectedProducts.size}")
                    }
                }

                holder.image.setOnClickListener {
                    if(holder.checkBtn.isChecked == true){
                        holder.checkBtn.isChecked = false
                        holder.cardView.alpha = 0.4F
                        (fragment as OrderReturnFragment).selectedProducts.remove(model)
                        Log.e(TAG, "onBindViewHolder: ${(fragment as OrderReturnFragment).selectedProducts.size}")
                    }else{
                        holder.cardView.alpha = 1F
                        holder.checkBtn.isChecked = true
                        (fragment as OrderReturnFragment).selectedProducts.add(model)

                        Log.e(TAG, "onBindViewHolder: ${(fragment as OrderReturnFragment).selectedProducts.size}")
                    }
                }






                var quantity:Int = model.cartQuantity.toInt()
                holder.add.setOnClickListener {

                    if(holder.checkBtn.isChecked){
                        if(holder.quality.text.toString() == model.cartQuantity.toString()) {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "You can't return products more than you ordered.", Snackbar.LENGTH_SHORT)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()

                    }else{
                        quantity++
                        holder.quality.text = quantity.toString()
                            model.cartQuantity = quantity.toString()
                    }
                    }}


                holder.sub.setOnClickListener {

                    if(holder.checkBtn.isChecked){
                        if(quantity > 1){
                            quantity--
                            holder.quality.text = quantity.toString()
                            model.cartQuantity = quantity.toString()
                        }else{
                            val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "You must have to select at least 1 quantity.", Snackbar.LENGTH_SHORT)
                            snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                            snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                            snackBar.show()
                            fragment.vibratePhone()
                        }
                    }



                }



            }




        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}