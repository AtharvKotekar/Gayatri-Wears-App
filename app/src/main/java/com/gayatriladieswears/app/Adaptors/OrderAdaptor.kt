package com.gayatriladieswears.app.Adaptors

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.Fragments.OrderAddressFragment
import com.gayatriladieswears.app.Model.Address
import com.gayatriladieswears.app.Model.Order
import com.gayatriladieswears.app.OrderFragment
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
        holder.itemView.findViewById<TextView>(R.id.order_status).text = model.orderStatus
        holder.itemView.findViewById<Button>(R.id.order_track_btn).setOnClickListener {
            if (model.orderStatus.toString() == "Packing") {
                val snackBar = Snackbar.make(
                    fragment.requireActivity().findViewById(android.R.id.content),
                    "Order can be only tracked after Handover to courier.",
                    Snackbar.LENGTH_LONG
                )
                snackBar.setBackgroundTint(fragment.requireActivity().getColor(R.color.red))
                snackBar.setTextColor(fragment.requireActivity().getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
            }else if (model.orderStatus.toString() == "Packed") {
                val snackBar = Snackbar.make(
                    fragment.requireActivity().findViewById(android.R.id.content),
                    "Order can be only tracked after Handover to courier.",
                    Snackbar.LENGTH_LONG
                )
                snackBar.setBackgroundTint(fragment.requireActivity().getColor(R.color.red))
                snackBar.setTextColor(fragment.requireActivity().getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
            }else{
                val url = "https://apiv2.shiprocket.in/v1/external/courier/track/awb/${model.orderId}"
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(context.resources.getColor(R.color.black))
                builder.addDefaultShareMenuItem()

                val anotherCustomTab = CustomTabsIntent.Builder().build()

                val intent = anotherCustomTab.intent
                intent.data = Uri.parse("https://apiv2.shiprocket.in/v1/external/courier/track/awb/${model.orderId}")

                builder.setShowTitle(true)

                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, Uri.parse(url))
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }
}