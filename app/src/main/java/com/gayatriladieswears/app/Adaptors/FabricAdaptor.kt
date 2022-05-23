package com.gayatriladieswears.app.Adaptors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.Model.Info
import com.gayatriladieswears.app.R

class FabricAdaptor(private val context: Context, private var list: ArrayList<Info>,private val fromColor:Boolean) :  RecyclerView.Adapter<FabricAdaptor.myViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return FabricAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.fabric_iteam, parent, false)
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

        holder.text.text = model.name

        holder.itemView.setOnClickListener {
            if(fromColor){
                val bundle = Bundle()
                bundle.putString("from","color")
                bundle.putString("title",model.name)
                if (holder.itemView.findNavController().currentDestination?.id == R.id.homeFragment) {
                    holder.itemView.findNavController().navigate(R.id.action_homeFragment_to_shopingFragment2,bundle)
                }

            }
            else{
                val bundle = Bundle()
                bundle.putString("from","fabric")
                bundle.putString("title",model.name)
                if (holder.itemView.findNavController().currentDestination?.id == R.id.homeFragment) {
                    holder.itemView.findNavController().navigate(R.id.action_homeFragment_to_shopingFragment2,bundle)
                }

            }

        }

    }

    override fun getItemCount(): Int {
        return  list.size
    }

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image = view.findViewById<ImageView>(R.id.fabric_image)
        var text = view.findViewById<TextView>(R.id.fabric_text)
    }

}