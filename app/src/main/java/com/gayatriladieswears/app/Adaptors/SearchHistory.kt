package com.gayatriladieswears.app.Adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Fragments.OrderAddressFragment
import com.gayatriladieswears.app.Model.Address
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.SearchFrsgment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

class SearchHistory(private val context: Context, private var fragment: SearchFrsgment, private var list: ArrayList<String>) : RecyclerView.Adapter<SearchHistory.myViewHolder>() {

    class myViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var name = view.findViewById<TextView>(R.id.search_text)
        var remove = view.findViewById<ImageView>(R.id.searc_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return SearchHistory.myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recent_search_iteam, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val model = list[position]
        holder.name.text = model.toString()
        holder.itemView.setOnClickListener {
            fragment.search(model)
        }
        holder.remove.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(context,R.style.AppCompatAlertDialogStyle)
            dialog.setTitle("Remove Recent Search")
            dialog.setMessage("Do you really want to remove recent search?")
            dialog.background = context.resources.getDrawable(R.drawable.black_btn_bg)
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            dialog.setPositiveButton("Remove") { dialog, which ->
                FirestoreClass().mFirestore.collection("Cache")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("recentSearches",FieldValue.arrayRemove(model))
                FirestoreClass().getRecentSearches(fragment)
            }
            dialog.show()
        }
        }




    override fun getItemCount(): Int {
       return list.size
    }


}