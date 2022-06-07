package com.gayatriladieswears.app.Adaptors

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.gayatriladieswears.app.Fragments.ShopingFragment
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.R

class ProductsAdaptor(private val context:Context, private var list: ArrayList<Product>,private var fragment: Fragment) : RecyclerView.Adapter<ProductsAdaptor.myViewHolder>() {

    init {
        when(fragment){
            is ShopingFragment -> {
                if(list.isEmpty()){
                    (fragment as ShopingFragment).getLenght("0")
                }
                else{
                    (fragment as ShopingFragment).getLenght((list.size).toString())
                }
                Log.i(TAG, "onBindViewHolder: ${list.size} ")
            }

        }
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {


        return ProductsAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.product_iteam, parent, false)

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

        if(model.stock == 0){
            holder.stock.visibility = View.VISIBLE
            holder.stock.text = "Out of Stock"
        }else if(model.stock < 5){
            holder.stock.visibility = View.VISIBLE
            holder.stock.text = "Limited Stock"
        }else{
            holder.stock.visibility = View.GONE
        }

        holder.title.text = model.name
        holder.des.text = model.dis
        holder.price.text = model.price.toString()
        holder.sizes.adapter = ProductSizeAdaptor(context,model.size)
        holder.sizes.layoutManager =  LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL,false)
        holder.mrp.text = model.mrp.toString()
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("name",model.name)
            bundle.putString("price",model.price.toString())
            bundle.putString("dis",model.dis)
            bundle.putString("fabric",model.fabric)
            bundle.putString("image",model.image)
            bundle.putString("category",model.category)
            bundle.putString("color",model.color)
            bundle.putString("pattern",model.pattern)
            bundle.putString("occasion",model.occasion)
            bundle.putString("mrp",model.mrp.toString())
            bundle.putString("id",model.id)
            bundle.putString("stock",model.stock.toString())
            bundle.putStringArrayList("sizes",model.size)
            bundle.putStringArrayList("tag",model.tag)

            holder.itemView.findNavController().navigate(R.id.action_shopingFragment_to_productDetailFragment,bundle)

        }



    }

    override fun getItemCount(): Int {
        return list.size
    }

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image = view.findViewById<ImageView>(R.id.imageView11)
        val title = view.findViewById<TextView>(R.id.spotlight_title)
        val des = view.findViewById<TextView>(R.id.spotlight_des)
        val price = view.findViewById<TextView>(R.id.spotlight_price)
        val sizes = view.findViewById<RecyclerView>(R.id.recyclerView_size)
        val mrp = view.findViewById<TextView>(R.id.spotlight_mrp)
        val stock = view.findViewById<TextView>(R.id.spotlight_stock_text)
    }





}