package com.example.subtrack.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.subtrack.MainActivity
import com.example.subtrack.R
import com.example.subtrack.databinding.FragmentOwnerHomeBinding

class OwnerHomeFragment : Fragment() {
    private var _binding: FragmentOwnerHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOwnerHomeBinding.inflate(inflater, container, false)

        (activity as MainActivity).hideBottomNavigation(false)

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {}

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

