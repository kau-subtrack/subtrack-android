package com.please.ui.driver.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.please.databinding.FragmentDriverMapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverMapFragment : Fragment() {

    private var _binding: FragmentDriverMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 추후 지도 관련 설정이 필요한 경우 여기에 추가
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
