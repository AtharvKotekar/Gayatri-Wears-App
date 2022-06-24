package com.gayatriladieswears.app.Adaptors

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.Model.Order
import com.gayatriladieswears.app.Fragments.OrderFragment
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar

class OrderAdaptor(private val context: Context, private var fragment: OrderFragment, private var list: ArrayList<Order>) : RecyclerView.Adapter<OrderAdaptor.myViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_iteam, parent, false)
        return OrderAdaptor.myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]
        Log.e(TAG, "onBindViewHolder: $model")

        holder.itemView.findViewById<TextView>(R.id.order_id).text = model.orderId
        holder.itemView.findViewById<TextView>(R.id.order_transactionId).text = model.transactionId
        holder.itemView.findViewById<TextView>(R.id.order_quantity).text = model.totalQuantity
        holder.itemView.findViewById<TextView>(R.id.order_price).text = model.amout.toString()
        holder.itemView.findViewById<TextView>(R.id.order_date_text).text = model.date
        holder.itemView.findViewById<Button>(R.id.order_track_btn).setOnClickListener {
            val url = "https://shiprocket.co/tracking/${model.courierId}"
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(context.resources.getColor(R.color.black))
            builder.addDefaultShareMenuItem()

            val anotherCustomTab = CustomTabsIntent.Builder().build()

            val intent = anotherCustomTab.intent
            intent.data = Uri.parse("https://shiprocket.co/tracking/${model.courierId}")

            builder.setShowTitle(true)

            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("orderId",model.orderId)
            bundle.putString("transactionId",model.transactionId)
            bundle.putString("date",model.date)
            bundle.putString("name",model.name)
            bundle.putString("address",model.address)
            bundle.putString("landmark",model.landMark)
            bundle.putString("contact",model.contact)
            bundle.putString("tag",model.tag)
            bundle.putParcelableArrayList("products",model.orderedProducts)
            bundle.putString("amount",model.amout.toString())
            bundle.putString("awb",model.courierId)
            bundle.putString("pincode",model.pincode)
            bundle.putString("email",model.email)

            holder.itemView.findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment,bundle)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }
}