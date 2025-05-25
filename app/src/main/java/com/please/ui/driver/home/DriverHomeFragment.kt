package com.please.ui.driver.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.please.databinding.FragmentDriverHomeBinding
import com.please.ui.driver.home.DriverHomeViewModel.HomeInfoState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverHomeFragment : Fragment() {

    private var _binding: FragmentDriverHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DriverHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        // 포인트 카드 클릭 시 데이터 새로고침
        binding.pointsCard.setOnClickListener {
            Toast.makeText(requireContext(), "데이터를 새로고침 중...", Toast.LENGTH_SHORT).show()
            viewModel.refreshData()
        }
    }

    private fun setupObservers() {
        viewModel.homeInfoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeInfoState.Loading -> {
                    // 로딩 상태 처리
                    Log.d("DRIVER_HOME/UI", "Loading...")
                    Toast.makeText(requireContext(), "데이터 로딩 중...", Toast.LENGTH_SHORT).show()
                }
                is HomeInfoState.Success -> {
                    // 성공 시 UI 업데이트
                    Log.d("DRIVER_HOME/UI", "Success: ${state.data}")
                    updateUI(state.data)
                }
                is HomeInfoState.Error -> {
                    // 에러 처리
                    Log.d("DRIVER_HOME/UI", "Error: ${state.message}")
                    Toast.makeText(requireContext(), "오류: ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateUI(response: com.please.data.models.driver.DriverHomeResponse) {
        val data = response.data
        binding.apply {
            // 담당 위치 업데이트 - 빈 값 처리
            tvLocation.text = if (data.region.isNullOrBlank()) {
                "지역 정보 없음"
            } else {
                data.region
            }

            // 이번 달 수행 건수 업데이트
            tvMonthlyPickup.text = data.monthlyCount.pickup.toString()
            tvMonthlyDelivery.text = data.monthlyCount.delivery.toString()
            tvMonthlyTotal.text = data.monthlyCount.total.toString()

            // 오늘 업무 업데이트 - 0값 처리 (필요시 "업무 없음" 등의 메시지 표시 가능)
            tvTodayPickup.text = if (data.todayCount.pickup == 0) {
                "0"
            } else {
                data.todayCount.pickup.toString()
            }
            
            tvTodayDelivery.text = if (data.todayCount.delivery == 0) {
                "0"
            } else {
                data.todayCount.delivery.toString()
            }

            // 적립 포인트 업데이트 - 0값 처리
            tvPoints.text = if (data.points == 0) {
                "0"
            } else {
                data.points.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
