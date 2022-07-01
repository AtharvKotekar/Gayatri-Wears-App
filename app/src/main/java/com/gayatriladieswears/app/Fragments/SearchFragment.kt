package com.gayatriladieswears.app

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gayatriladieswears.app.Adaptors.AddressAdaptor
import com.gayatriladieswears.app.Adaptors.SearchHistory
import com.gayatriladieswears.app.databinding.FragmentSearchBinding


class SearchFrsgment : Fragment() {

    lateinit var binding:FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater,container,false)

        binding.textField.requestFocus()
        showSoftKeyboard(binding.editText)

        FirestoreClass().getRecentSearches(this)

        binding.textField.setStartIconOnClickListener {
            if(binding.editText.text.toString() == ""){
                binding.editText.setHint("Please enter product name here.")
                binding.editText.setHintTextColor(resources.getColor(R.color.red))
                vibratePhone()
            }else{
                search(binding.editText.text.toString())
            }

        }




        binding.editText.afterTextChangedDelayed {
            val names: ArrayList<String> = ArrayList()

            FirestoreClass().mFirestore.collection("Products")
                .whereArrayContains("keywords", binding.editText.text.toString().lowercase())
                .get()
                .addOnSuccessListener {
                    for (i in it.documents) {
                        names.add(i.getString("name").toString())
                    }
                    Log.i(TAG, "onCreateView: $names")
                    ArrayAdapter<String>(
                        context!!,
                        android.R.layout.simple_dropdown_item_1line,
                        names
                    ).also { adapter ->
                        binding.editText.setAdapter(adapter)
                    }

                }
        }



        binding.editText.setOnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_SEARCH){
                if(textView.text.toString() == ""){
                    binding.editText.setHint("Please enter product name here.")
                    binding.editText.setHintTextColor(resources.getColor(R.color.red))
                    vibratePhone()
                }else{
                    search(textView.text.toString())
                    FirestoreClass().addtorecentSearch(this,textView.text.toString())
                }
                return@setOnEditorActionListener true
            }else{
                return@setOnEditorActionListener false
            }

        }






        return binding.root
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun getRecentSearches(iteamList:ArrayList<String>){
        val adaptor = SearchHistory(requireContext(),this,iteamList)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
    }

    fun search(text:String){
        val bundle = Bundle()
        bundle.putBoolean("fromSearch",true)
        bundle.putString("searchQuery",text)
        findNavController().navigate(R.id.action_searchFrsgment_to_shopingFragment,bundle)
        FirestoreClass().addtorecentSearch(this,text)

    }


    fun AutoCompleteTextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(10, 100) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        afterTextChanged.invoke(editable.toString())
                    }
                }.start()
            }
        })
    }

}