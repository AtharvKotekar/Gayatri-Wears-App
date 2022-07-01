package com.gayatriladieswears.app.Adaptors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.gayatriladieswears.app.Model.Info
import com.gayatriladieswears.app.R

open class TopCategoriesAdaptor(private val fragment:HomeFragment,private val context: Context,private var list: ArrayList<Info>) : RecyclerView.Adapter<TopCategoriesAdaptor.myViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.top_category_iteam,parent,false)
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
            val bundle = Bundle()
            bundle.putString("from","category")
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
        val image = view.findViewById<ImageView>(R.id.image_top_caegories)
        var text = view.findViewById<TextView>(R.id.infotext)
    }

}