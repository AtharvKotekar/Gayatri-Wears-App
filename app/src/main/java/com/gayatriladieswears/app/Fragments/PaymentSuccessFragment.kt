package com.gayatriladieswears.app.Fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentPaymentSuccessBinding

class PaymentSuccessFragment : Fragment() {

    private lateinit var binding: FragmentPaymentSuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentSuccessBinding.inflate(inflater,container,false)

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_paymentSuccessFragment_to_orderFragment)
        }

        var  mediaPlayer = MediaPlayer.create(requireContext(), R.raw.payment_success_sound)
        mediaPlayer.start()
        return binding.root
    }

}