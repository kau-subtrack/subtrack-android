package com.please.ui.driver.collect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.please.databinding.FragmentDriverCollectBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverCollectFragment : Fragment() {

    private var _binding: FragmentDriverCollectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverCollectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 초기화 및 UI 설정
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}