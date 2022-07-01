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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.*
import com.gayatriladieswears.app.Model.Order
import com.gayatriladieswears.app.Fragments.OrderFragment
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.Model.Token
import com.gayatriladieswears.app.Model.TokenCall
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.order_iteam.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap

class OrderAdaptor(private val context: Context, private var fragment: OrderFragment, private var list: ArrayList<Order>) : RecyclerView.Adapter<OrderAdaptor.myViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_iteam, parent, false)
        return OrderAdaptor.myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]
        Log.e(TAG, "onBindViewHolder: $model")

        var awb:String = ""

        holder.itemView.order_id.text = model.orderId
        holder.itemView.order_transactionId.text = model.transactionId
        holder.itemView.order_quantity.text = model.totalQuantity
        holder.itemView.order_price.text = model.amout.toString()
        holder.itemView.order_date_text.text = model.date





        if("R" in model.orderId){
            holder.itemView.findViewById<Button>(R.id.order_track_btn).visibility= View.GONE
            holder.itemView.findViewById<TextView>(R.id.textView19).text = "Returned"
            holder.itemView.findViewById<TextView>(R.id.textView19).setTextColor(context.resources.getColor(R.color.gray))
            holder.itemView.findViewById<TextView>(R.id.textView19).visibility = View.VISIBLE
            fragment.dialog.dismiss()

            holder.itemView.setOnClickListener {
                var totaAmount = 0
                var shippingCharges = "0"

                for ( i in model.orderedProducts){
                    totaAmount = totaAmount + i.price
                }

                if(totaAmount < 799){
                    shippingCharges = "99"
                }else{
                    shippingCharges = "0"

                }

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
                bundle.putString("awb","")
                bundle.putString("pincode",model.pincode)
                bundle.putString("email",model.email)
                bundle.putString("shippingCharges",shippingCharges)

                holder.itemView.findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment,bundle)
            }

        }else if("C" in model.orderId){
            holder.itemView.findViewById<Button>(R.id.order_track_btn).visibility = View.GONE
            holder.itemView.findViewById<TextView>(R.id.textView19).text = "Canceled"
            holder.itemView.findViewById<TextView>(R.id.textView19).setTextColor(context.resources.getColor(R.color.red))
            holder.itemView.findViewById<TextView>(R.id.textView19).visibility = View.VISIBLE
            fragment.dialog.dismiss()

            holder.itemView.setOnClickListener {
                var totaAmount = 0
                var shippingCharges = "0"

                for ( i in model.orderedProducts){
                    totaAmount = totaAmount + i.price
                }

                if(totaAmount < 799){
                    shippingCharges = "99"
                }else{
                    shippingCharges = "0"

                }

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
                bundle.putString("shippingCharges",shippingCharges)

                holder.itemView.findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment,bundle)
            }

        }else if(model.courierId.toString() == ""){
            holder.itemView.order_track_btn.visibility = View.GONE
            holder.itemView.textView19.text = "Packing"
            holder.itemView.textView19.setTextColor(context.resources.getColor(R.color.green))
            holder.itemView.textView19.visibility = View.VISIBLE




            CoroutineScope(IO).launch {
                val retrofitBuilder = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()

                val orderServices = retrofitBuilder.create(OrderServices::class.java)

                val tokenc = Token("gayatriauth@gmail.com","Gayatri@2022")
                val call = orderServices.getToken(tokenc)

                call.enqueue(object:Callback<TokenCall>{
                    override fun onResponse(call: Call<TokenCall>, response: Response<TokenCall>) {
                        val token = "Bearer ${response.body()!!.token}"

                        val headerMap: HashMap<String, String> = HashMap<String,String>()
                        headerMap["Content-Type"] = "application/json"
                        headerMap["Authorization"] = token

                        val ordercall = orderServices.getOrderDetail(headerMap,model.orderId)
                        ordercall.enqueue(object:Callback<JsonObject>{
                            override fun onResponse(
                                call: Call<JsonObject>,
                                response2: Response<JsonObject>
                            ) {
                                if(response.isSuccessful){
                                    val data = response2.body()?.getAsJsonObject("data")?.getAsJsonObject("awb_data")
                                    Log.e(TAG, "onResponse: $data", )
                                    awb = data?.get("awb").toString()
                                    if(awb.replace("\"", "") == ""){
                                        holder.itemView.order_track_btn.visibility = View.GONE
                                        holder.itemView.textView19.text = "Packing"
                                        holder.itemView.textView19.setTextColor(context.resources.getColor(R.color.green))
                                        holder.itemView.textView19.visibility = View.VISIBLE


                                        holder.itemView.setOnClickListener {
                                            var totaAmount = 0
                                            var shippingCharges = "0"

                                            for ( i in model.orderedProducts){
                                                totaAmount = totaAmount + i.price
                                            }

                                            if(totaAmount < 799){
                                                shippingCharges = "99"
                                            }else{
                                                shippingCharges = "0"

                                            }

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
                                            bundle.putString("awb","")
                                            bundle.putString("pincode",model.pincode)
                                            bundle.putString("email",model.email)
                                            bundle.putString("shippingCharges",shippingCharges)

                                            holder.itemView.findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment,bundle)
                                        }
                                        fragment.dialog.dismiss()

                                    }else{

                                        model.courierId = awb.replace("\"", "")

                                        holder.itemView.order_track_btn.visibility = View.VISIBLE
                                        holder.itemView.textView19.visibility = View.GONE
                                        fragment.dialog.dismiss()
                                        FirestoreClass().mFirestore.collection("Orders")
                                            .document(model.orderId.replace("R","").replace("C",""))
                                            .update("courierId",awb.replace("\"", ""))



                                        holder.itemView.order_track_btn.setOnClickListener {
                                            val url = "https://shiprocket.co/tracking/$awb"
                                            val builder = CustomTabsIntent.Builder()
                                            builder.setToolbarColor(context.resources!!.getColor(R.color.black))
                                            builder.addDefaultShareMenuItem()

                                            val anotherCustomTab = CustomTabsIntent.Builder().build()

                                            val intent = anotherCustomTab.intent
                                            intent.data = Uri.parse("https://shiprocket.co/tracking/$awb")

                                            builder.setShowTitle(true)

                                            val customTabsIntent = builder.build()
                                            customTabsIntent.launchUrl(context, Uri.parse(url))
                                        }

                                        holder.itemView.setOnClickListener {
                                            var totaAmount = 0
                                            var shippingCharges = "0"

                                            for ( i in model.orderedProducts){
                                                totaAmount = totaAmount + i.price
                                            }

                                            if(totaAmount < 799){
                                                shippingCharges = "99"
                                            }else{
                                                shippingCharges = "0"

                                            }

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
                                            bundle.putString("shippingCharges",shippingCharges)

                                            holder.itemView.findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment,bundle)
                                        }

                                        fragment.dialog.dismiss()

                                    }
                                }else{
                                    fragment.dialog.dismiss()
                                    Log.e(TAG, "onResponse: ${response2.raw()}", )
                                    val snackBar = Snackbar.make(
                                        fragment.requireActivity().findViewById(android.R.id.content),
                                        "Something Went Wrong",
                                        Snackbar.LENGTH_LONG
                                    )
                                    snackBar.setBackgroundTint(context.resources.getColor(R.color.red))
                                    snackBar.setTextColor(context.resources.getColor(R.color.white))
                                    snackBar.show()
                                    fragment.vibratePhone()


                                }
                            }

                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                fragment.dialog.dismiss()
                                val snackBar = Snackbar.make(
                                    fragment.requireActivity().findViewById(android.R.id.content),
                                    "Something Went Wrong",
                                    Snackbar.LENGTH_LONG
                                )
                                snackBar.setBackgroundTint(context.resources.getColor(R.color.red))
                                snackBar.setTextColor(context.resources.getColor(R.color.white))
                                snackBar.show()
                                fragment.vibratePhone()
                            }

                        }
                        )
                    }

                    override fun onFailure(call: Call<TokenCall>, t: Throwable) {
                        fragment.dialog.dismiss()
                        val snackBar = Snackbar.make(
                            fragment.requireActivity().findViewById(android.R.id.content),
                            "Something Went Wrong",
                            Snackbar.LENGTH_LONG
                        )
                        snackBar.setBackgroundTint(context.resources.getColor(R.color.red))
                        snackBar.setTextColor(context.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                    }

                })
            }


        }
        else{

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
                var totaAmount = 0
                var shippingCharges = "0"

                for ( i in model.orderedProducts){
                    totaAmount = totaAmount + i.price
                }

                if(totaAmount < 799){
                    shippingCharges = "99"
                }else{
                    shippingCharges = "0"

                }

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
                bundle.putString("shippingCharges",shippingCharges)

                holder.itemView.findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment,bundle)
            }

            fragment.dialog.dismiss()



        }

    }

    override fun getItemCount(): Int {
        return list.size
    }



    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }
}