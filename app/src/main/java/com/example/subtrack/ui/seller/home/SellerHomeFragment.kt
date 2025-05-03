package com.example.subtrack.ui.seller.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.subtrack.R
import com.example.subtrack.databinding.FragmentSellerHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerHomeFragment : Fragment() {

    private var _binding: FragmentSellerHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SellerHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.homeInfoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SellerHomeViewModel.HomeInfoState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }
                is SellerHomeViewModel.HomeInfoState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    
                    val data = state.data
                    
                    // 가게 위치 정보 설정
                    binding.tvStoreAddress.text = data.storeInfo.address
                    
                    // 실제 앱에서는 지도 표시 로직 구현
                    // binding.mapView.setMapLocation(data.storeInfo.latitude, data.storeInfo.longitude)
                    
                    // 수거 날짜 설정
                    binding.tvPickupDate.text = data.pickupInfo.date
                    
                    // 배정 기사 정보 설정
                    binding.tvCourierName.text = data.courierInfo.name
                    binding.tvCourierPhone.text = data.courierInfo.phoneNumber
                    binding.tvCourierCar.text = data.courierInfo.carNumber
                    
                    // 포인트 정보 설정
                    binding.tvPoints.text = data.points.toString()
                    
                    // 구독 정보 설정
                    binding.tvSubscription.text = data.subscription
                }
                is SellerHomeViewModel.HomeInfoState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupListeners() {
        // 챗봇 버튼 클릭 이벤트
        binding.fabChatbot.setOnClickListener {
            // 챗봇 화면으로 이동
            findNavController().navigate(R.id.action_sellerHomeFragment_to_chatbotFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
