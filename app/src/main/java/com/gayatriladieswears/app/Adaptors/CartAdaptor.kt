package com.gayatriladieswears.app.Adaptors

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.Fragments.CheckOutFragment
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Fragments.CartFragment
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CartAdaptor(private val context: Context,private var fragment: Fragment,private var list: ArrayList<CartItem>) : RecyclerView.Adapter<CartAdaptor.myViewHolder>() {

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
        var stock = view.findViewById<TextView>(R.id.cart_stock_text)
        var quantity_text = view.findViewById<TextView>(R.id.quantity_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return CartAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.cart_iteam, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]

        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",model.productId)
            .get()
            .addOnSuccessListener {
                val iteamList: ArrayList<Product> = ArrayList()
                for (i in it.documents){
                    val iteam = i.toObject(Product::class.java)
                    iteamList.add(iteam!!)
                }
                if(iteamList.size == 1){
                    if (iteamList[0].stock == 0){
                        holder.stock.visibility = View.VISIBLE
                        holder.stock.text = "Out of Stock"

                        when(fragment){
                            is CartFragment -> {
                                (fragment as CartFragment).outOfStockPresent = true
                            }
                        }
                        
                    }else if(iteamList[0].stock < 5){
                        holder.stock.visibility = View.VISIBLE
                        holder.stock.text = "Limited Stock"
                    }else{
                        holder.stock.visibility = View.GONE
                    }
                }
            }



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
            is CartFragment -> {

                holder.quantity_text.visibility = View.GONE

                holder.image.setOnClickListener {
                    (fragment as CartFragment).dialog.show()
                    FirestoreClass().getProductById(fragment,model.productId,holder)
                }

                var quantity:Int = model.cartQuantity.toInt()
                holder.add.setOnClickListener {
                    quantity++
                    FirestoreClass().updateCart(fragment,quantity.toString(),model.userId,model.productId)
                    FirestoreClass().getCartProducts(fragment,model.userId)
                    (fragment as CartFragment).dialog.show()
                }

                holder.sub.setOnClickListener {
                    if(quantity > 1){
                        quantity--
                        FirestoreClass().updateCart(fragment,quantity.toString(),model.userId,model.productId)
                        FirestoreClass().getCartProducts(fragment,model.userId)
                        (fragment as CartFragment).dialog.show()
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
                    dialog.background = context.resources.getDrawable(R.drawable.black_btn_bg)
                    dialog.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    dialog.setPositiveButton("Remove") { dialog, which ->
                        FirestoreClass().removeCartProduct(fragment as CartFragment,model.productId,FirebaseAuth.getInstance().currentUser!!.uid)
                        FirestoreClass().getCartProducts(fragment,FirebaseAuth.getInstance().currentUser!!.uid)
                    }
                    dialog.show()
                }
            }

            is CheckOutFragment -> {
                holder.add.visibility = View.GONE
                holder.sub.visibility = View.GONE
                holder.delete.visibility = View.GONE
                holder.quality.visibility = View.GONE
                holder.quantity_text.text = "Quantity  -  ${model.cartQuantity}"
                holder.quantity_text.visibility = View.VISIBLE
            }

            is OrderDetailFragment -> {
                holder.add.visibility = View.GONE
                holder.sub.visibility = View.GONE
                holder.delete.visibility = View.GONE
                holder.quality.visibility = View.GONE
                holder.quantity_text.text = "Quantity  -  ${model.cartQuantity}"
                holder.quantity_text.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int {
        return  list.size
    }




}