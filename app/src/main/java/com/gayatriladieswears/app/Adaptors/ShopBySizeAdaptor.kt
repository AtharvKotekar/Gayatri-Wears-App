package com.gayatriladieswears.app.Adaptors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.Model.Sizes

class ShopBySizeAdaptor(private val context: Context, private var list: ArrayList<Sizes>): RecyclerView.Adapter<ShopBySizeAdaptor.myViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return ShopBySizeAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.shop_size_iteam, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]

        holder.text.text = model.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","size")
            bundle.putString("title",model.name)
            if (holder.itemView.findNavController().currentDestination?.id == R.id.homeFragment) {
                holder.itemView.findNavController().navigate(R.id.action_homeFragment_to_shopingFragment2,bundle)
            }



        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val text = view.findViewById<TextView>(R.id.size_text)
    }
}