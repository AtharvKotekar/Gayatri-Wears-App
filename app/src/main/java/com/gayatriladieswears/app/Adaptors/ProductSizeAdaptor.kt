package com.gayatriladieswears.app.Adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.R

class ProductSizeAdaptor(private val context: Context, private var list: ArrayList<String>): RecyclerView.Adapter<ProductSizeAdaptor.myViewHolder>() {

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val text = view.findViewById<TextView>(R.id.textView17)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return ProductSizeAdaptor.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.size_iteam, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]

        holder.text.text = model.toString()
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}