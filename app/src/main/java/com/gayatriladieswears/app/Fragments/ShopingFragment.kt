package com.gayatriladieswears.app.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.ContentValues.TAG
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.Adaptors.ProductsAdaptor
import com.gayatriladieswears.app.Adaptors.SpotlightOnAdaptor
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.Filter
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentHomeBinding
import com.gayatriladieswears.app.databinding.FragmentShopingBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.Query


class ShopingFragment : Fragment() {


    private lateinit var from:String
    private lateinit var title:String
    private lateinit var mAdaptor: ProductsAdaptor
    private lateinit var listChipSize:ArrayList<Chip>
    private lateinit var listChipPrice:ArrayList<Chip>
    private lateinit var listChipcategory:ArrayList<Chip>
    private lateinit var listChipMaterial:ArrayList<Chip>
    private lateinit var listChipPattern:ArrayList<Chip>
    private lateinit var listChipOccasion:ArrayList<Chip>
    private lateinit var filterListColor:MutableList<String>
    private lateinit var binding:FragmentShopingBinding
    private lateinit var mDialog:Dialog
    private lateinit var alc:ArrayList<CheckBox>
    private var filterListSize:String = ""
    private var filterPrice:Int = 0
    private var filterCategory:String = ""
    private var filterMaterial:String = ""
    private var filterPattern:String = ""
    private var filterOccasion:String = ""

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val metrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(metrics)
        binding = FragmentShopingBinding.inflate(inflater,container,false)

        binding.recyclerViewProduct.visibility = View.INVISIBLE
        binding.shimmerViewShopping.visibility = View.VISIBLE
        binding.shimmerViewShopping.startShimmer()


        from = arguments?.getString("from").toString()
        title = arguments?.getString("title").toString()
        binding.reasultNameText.text = "${from.capitalize()} - $title"
        FirestoreClass().getSpecifyProducts(this,from,title)

        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.shopingFragment) {
                activity?.onBackPressed()
            }

        }

        binding.cartBtn.setOnClickListener {
            findNavController().navigate(R.id.action_shopingFragment_to_cartFragment)
        }


        alc = ArrayList()
        listChipSize = ArrayList()
        listChipPrice = ArrayList()
        listChipcategory = ArrayList()
        listChipMaterial = ArrayList()
        listChipPattern = ArrayList()
        listChipOccasion = ArrayList()
        filterListColor = mutableListOf()



        mDialog = Dialog(requireContext())
        mDialog.setContentView(R.layout.laoding_dialog)
        mDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mDialog.setCancelable(false)
        mDialog.show()





        binding.sortBtn.isClickable = true

        binding.sortBtn.setOnClickListener {
            binding.sortBtn.isClickable = false
            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.sort, null)
            dialog.setContentView(view)
            dialog.setCancelable(true)
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.show()

            val hightolow = dialog.findViewById<TextView>(R.id.price_high_to_low)
            val lowtohigh = dialog.findViewById<TextView>(R.id.price_low_to_high)
            val discount = dialog.findViewById<TextView>(R.id.discount)

            hightolow?.setOnClickListener {
                FirestoreClass().getFilteredProduct(this,from,title,"price",Query.Direction.DESCENDING)
                dialog.dismiss()
                binding.sortBtn.isClickable = true
                binding.recyclerViewProduct.visibility = View.INVISIBLE
                binding.shimmerViewShopping.visibility = View.VISIBLE
                binding.shimmerViewShopping.startShimmer()
                mDialog.show()
            }

            lowtohigh?.setOnClickListener {
                FirestoreClass().getFilteredProduct(this,from,title,"price",Query.Direction.ASCENDING)
                dialog.dismiss()
                binding.sortBtn.isClickable = true
                binding.recyclerViewProduct.visibility = View.INVISIBLE
                binding.shimmerViewShopping.visibility = View.VISIBLE
                binding.shimmerViewShopping.startShimmer()
                mDialog.show()
            }
            discount?.setOnClickListener {
                FirestoreClass().getFilteredProduct(this,from,title,"mrp",Query.Direction.DESCENDING)
                binding.sortBtn.isClickable = true
                dialog.dismiss()
                binding.recyclerViewProduct.visibility = View.INVISIBLE
                binding.shimmerViewShopping.visibility = View.VISIBLE
                binding.shimmerViewShopping.startShimmer()
                mDialog.show()
            }

            dialog.setOnCancelListener {
                binding.sortBtn.isClickable = true
            }

            dialog.setOnDismissListener {
                binding.sortBtn.isClickable = true
            }
        }


        binding.filterBtn.isClickable = true

        binding.filterBtn.setOnClickListener {
            binding.filterBtn.isClickable = false
            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.filter_layout, null)
            val layout = view.findViewById<ConstraintLayout>(R.id.constraintLayout)
            layout.minHeight = metrics.heightPixels
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.behavior.peekHeight = metrics.heightPixels
            dialog.show()

            binding.textView16.visibility = View.GONE
            binding.imageView7.visibility = View.GONE
            binding.shopMoreBtn.visibility = View.GONE


            val close = view.findViewById<ImageView>(R.id.close)
            val cancel = view.findViewById<TextView>(R.id.cancel)
            val apply = view.findViewById<TextView>(R.id.apply)




            close.setOnClickListener {
                binding.filterBtn.isClickable = true
                dialog.dismiss()
            }

            cancel.setOnClickListener {
                binding.filterBtn.isClickable = true
                dialog.dismiss()
            }


            apply.setOnClickListener {
                binding.filterBtn.isClickable = true
                filterListColor.clear()
                addCheckBoxes(view)
                for (i in alc){
                    if (i.isChecked){
                        filterListColor.add(i.text.toString())
                        Log.i(TAG, "onCreateView: ${i.text}")
                    }
                }
                for (i in listChipSize) {
                    if(i.isChecked) {
                        filterListSize = i.text.toString()
                    }

                }
                for (i in listChipPrice){
                    if (i.isChecked){
                        var text = i.text.toString()
                        text = text.replace("Under ","")
                        filterPrice =  text.toInt()
                        Log.i(TAG, "onCreateView: $filterPrice")
                    }
                }
                for (i in listChipcategory){
                    if(i.isChecked){
                        filterCategory = i.text.toString()
                    }
                }
                for (i in listChipMaterial){
                    if (i.isChecked){
                        filterMaterial = i.text.toString()
                    }
                }
                for (i in listChipPattern){
                    if (i.isChecked){
                        filterPattern = i.text.toString()
                        Log.i(TAG, "onCreateView: ${i.text.toString()}")
                    }
                }
                for(i in listChipOccasion){
                    if(i.isChecked){
                        filterOccasion = i.text.toString()
                    }
                }

                if(filterListColor.isEmpty()){
                    if(filterCategory == ""){
                        if (filterListSize == ""){
                            if(filterPrice == 0){
                                if(filterMaterial == ""){
                                    if (filterPattern == ""){
                                        if(filterOccasion == ""){
                                            FirestoreClass().getSpecifyProducts(this,from,title)
                                        }else{
                                            FirestoreClass().getFilterDialogProducts(this, filterListColor,filterListSize,filterPrice,filterCategory,filterMaterial,filterPattern,filterOccasion)
                                            dialog.dismiss()
                                            binding.recyclerViewProduct.visibility = View.INVISIBLE
                                            binding.shimmerViewShopping.visibility = View.VISIBLE
                                            binding.shimmerViewShopping.startShimmer()
                                            mDialog.show()
                                        }
                                    }else{
                                        FirestoreClass().getFilterDialogProducts(this, filterListColor,filterListSize,filterPrice,filterCategory,filterMaterial,filterPattern,filterOccasion)
                                        dialog.dismiss()
                                        binding.recyclerViewProduct.visibility = View.INVISIBLE
                                        binding.shimmerViewShopping.visibility = View.VISIBLE
                                        binding.shimmerViewShopping.startShimmer()
                                        mDialog.show()
                                    }
                                }else{
                                    FirestoreClass().getFilterDialogProducts(this, filterListColor,filterListSize,filterPrice,filterCategory,filterMaterial,filterPattern,filterOccasion)
                                    dialog.dismiss()
                                    binding.recyclerViewProduct.visibility = View.INVISIBLE
                                    binding.shimmerViewShopping.visibility = View.VISIBLE
                                    binding.shimmerViewShopping.startShimmer()
                                    mDialog.show()
                                }
                            }else{
                                FirestoreClass().getFilterDialogProducts(this, filterListColor,filterListSize,filterPrice,filterCategory,filterMaterial,filterPattern,filterOccasion)
                                dialog.dismiss()
                                binding.recyclerViewProduct.visibility = View.INVISIBLE
                                binding.shimmerViewShopping.visibility = View.VISIBLE
                                binding.shimmerViewShopping.startShimmer()
                                mDialog.show()
                            }
                        }else{
                            FirestoreClass().getFilterDialogProducts(this, filterListColor,filterListSize,filterPrice,filterCategory,filterMaterial,filterPattern,filterOccasion)
                            dialog.dismiss()
                            binding.recyclerViewProduct.visibility = View.INVISIBLE
                            binding.shimmerViewShopping.visibility = View.VISIBLE
                            binding.shimmerViewShopping.startShimmer()
                            mDialog.show()
                        }
                    }else{
                        FirestoreClass().getFilterDialogProducts(this, filterListColor,filterListSize,filterPrice,filterCategory,filterMaterial,filterPattern,filterOccasion)
                        dialog.dismiss()
                        binding.recyclerViewProduct.visibility = View.INVISIBLE
                        binding.shimmerViewShopping.visibility = View.VISIBLE
                        binding.shimmerViewShopping.startShimmer()
                        mDialog.show()
                    }
                }else{
                    FirestoreClass().getFilterDialogProducts(this, filterListColor,filterListSize,filterPrice,filterCategory,filterMaterial,filterPattern,filterOccasion)
                    dialog.dismiss()
                    binding.recyclerViewProduct.visibility = View.INVISIBLE
                    binding.shimmerViewShopping.visibility = View.VISIBLE
                    binding.shimmerViewShopping.startShimmer()
                    mDialog.show()
                }
                filterListColor.clear()
                filterPrice = 0
                filterListSize = ""
                filterCategory = ""
                filterMaterial = ""
                filterPattern = ""
                filterPattern = ""
                listChipPrice.clear()
                listChipSize.clear()
                listChipMaterial.clear()
                listChipPattern.clear()
                alc.clear()
                listChipcategory.clear()
                listChipOccasion.clear()
            }

            dialog.setOnCancelListener {
                binding.filterBtn.isClickable = true
            }

            dialog.setOnDismissListener {
                binding.filterBtn.isClickable = true
            }
        }

        return binding.root
    }

    fun getProducts(iteamList: ArrayList<Product>){
        if(iteamList.isEmpty()){
            mAdaptor = ProductsAdaptor(requireContext(),iteamList,this)
            binding.recyclerViewProduct.adapter = mAdaptor
            binding.textView16.visibility = View.VISIBLE
            binding.imageView7.visibility = View.VISIBLE
            binding.shopMoreBtn.visibility = View.VISIBLE
            mDialog.dismiss()
            binding.recyclerViewProduct.visibility = View.VISIBLE
            binding.shimmerViewShopping.visibility = View.INVISIBLE
            binding.shimmerViewShopping.stopShimmer()
        }else{
            mAdaptor = ProductsAdaptor(requireContext(),iteamList,this)
            binding.recyclerViewProduct.adapter = mAdaptor
            binding.recyclerViewProduct.layoutManager = GridLayoutManager(requireActivity(),2,LinearLayoutManager.VERTICAL,false)
            mDialog.dismiss()
            binding.recyclerViewProduct.visibility = View.VISIBLE
            binding.shimmerViewShopping.visibility = View.INVISIBLE
            binding.shimmerViewShopping.stopShimmer()
        }


    }

    fun addCheckBoxes(view: View){
        alc.add(view.findViewById(R.id.pink_checkbox))
        alc.add(view.findViewById(R.id.blue_checkbox))
        alc.add(view.findViewById(R.id.green_checkbox))
        alc.add(view.findViewById(R.id.black_checkbox))
        alc.add(view.findViewById(R.id.yellow_checkbox))
        alc.add(view.findViewById(R.id.white_checkbox))
        alc.add(view.findViewById(R.id.red_checkbox))
        alc.add(view.findViewById(R.id.grey_checkbox))
        alc.add(view.findViewById(R.id.navyblue_checkbox))
        alc.add(view.findViewById(R.id.orange_checkbox))

        listChipSize.add(view.findViewById(R.id.chip_xs))
        listChipSize.add(view.findViewById(R.id.chip_s))
        listChipSize.add(view.findViewById(R.id.chip_m))
        listChipSize.add(view.findViewById(R.id.chip_l))
        listChipSize.add(view.findViewById(R.id.chip_xl))
        listChipSize.add(view.findViewById(R.id.chip_xxl))

        listChipPrice.add(view.findViewById(R.id.chip_under_499))
        listChipPrice.add(view.findViewById(R.id.chip_under_799))
        listChipPrice.add(view.findViewById(R.id.chip_under_999))
        listChipPrice.add(view.findViewById(R.id.chip_under_1499))
        listChipPrice.add(view.findViewById(R.id.chip_under_1799))
        listChipPrice.add(view.findViewById(R.id.chip_under_1999))
        listChipPrice.add(view.findViewById(R.id.chip_under_2499))
        listChipPrice.add(view.findViewById(R.id.chip_under_2999))
        listChipPrice.add(view.findViewById(R.id.chip_under_3999))

        listChipcategory.add(view.findViewById(R.id.chip_salwar_kameez))
        listChipcategory.add(view.findViewById(R.id.chip_kurti))
        listChipcategory.add(view.findViewById(R.id.chip_dress_material))
        listChipcategory.add(view.findViewById(R.id.chip_chuddidar))
        listChipcategory.add(view.findViewById(R.id.chip_cigar_pant_dress))
        listChipcategory.add(view.findViewById(R.id.chip_legins))

        listChipMaterial.add(view.findViewById(R.id.chip_cotton))
        listChipMaterial.add(view.findViewById(R.id.chip_silk))
        listChipMaterial.add(view.findViewById(R.id.chip_rayon))
        listChipMaterial.add(view.findViewById(R.id.chip_georgette))
        listChipMaterial.add(view.findViewById(R.id.chip_organza))
        listChipMaterial.add(view.findViewById(R.id.chip_chiffon))
        listChipMaterial.add(view.findViewById(R.id.chip_viscose))

        listChipPattern.add(view.findViewById(R.id.chip_printed))
        listChipPattern.add(view.findViewById(R.id.chip_floral))
        listChipPattern.add(view.findViewById(R.id.chip_solid))
        listChipPattern.add(view.findViewById(R.id.chip_geometric))
        listChipPattern.add(view.findViewById(R.id.chip_checks))
        listChipPattern.add(view.findViewById(R.id.chip_detailing))


        listChipOccasion.add(view.findViewById(R.id.chip_festival))
        listChipOccasion.add(view.findViewById(R.id.chip_casual))
        listChipOccasion.add(view.findViewById(R.id.chip_party))
        listChipOccasion.add(view.findViewById(R.id.chip_wedding))
        listChipOccasion.add(view.findViewById(R.id.chip_semi_formal))

    }


    @SuppressLint("SetTextI18n")
    fun getLenght(lenght: String) {
        binding.resultSizeText.text = "Total "+lenght+" Results"
    }





}

