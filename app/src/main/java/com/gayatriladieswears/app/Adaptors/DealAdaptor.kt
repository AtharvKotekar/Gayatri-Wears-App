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
import com.gayatriladieswears.app.Model.Deal
import com.gayatriladieswears.app.R

class DealAdaptor(private val context: Context, private var list: ArrayList<Deal>): RecyclerView.Adapter<DealAdaptor.myViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return DealAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.deal_iteam, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]

        holder.prefix.text = model.prefix
        holder.deal.text = model.deal
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from","deal")
            bundle.putString("title",model.prefix + " " + model.deal)
            if (holder.itemView.findNavController().currentDestination?.id == R.id.homeFragment) {
                holder.itemView.findNavController().navigate(R.id.action_homeFragment_to_shopingFragment2,bundle)
            }

        }
    }

    override fun getItemCount(): Int {
      return  list.size
    }


    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val prefix = view.findViewById<TextView>(R.id.prefix_text)
        val deal = view.findViewById<TextView>(R.id.deal_text)
    }
}