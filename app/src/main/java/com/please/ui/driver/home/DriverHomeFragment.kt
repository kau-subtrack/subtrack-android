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
    }

    private fun setupObservers() {
        viewModel.homeInfoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeInfoState.Loading -> {
                    // 로딩 상태 처리 (필요하다면 ProgressBar 표시)
                    Log.d("DRIVER_HOME/UI", "Loading...")
                }
                is HomeInfoState.Success -> {
                    // 성공 시 UI 업데이트
                    Log.d("DRIVER_HOME/UI", "Success: ${state.data}")
                    updateUI(state.data)
                }
                is HomeInfoState.Error -> {
                    // 에러 처리
                    Log.d("DRIVER_HOME/UI", "Error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(response: com.please.data.models.driver.DriverHomeResponse) {
        val data = response.data
        binding.apply {
            // 담당 위치 업데이트
            tvLocation.text = data.region

            // 이번 달 수행 건수 업데이트
            tvMonthlyPickup.text = data.monthlyCount.pickup.toString()
            tvMonthlyDelivery.text = data.monthlyCount.delivery.toString()
            tvMonthlyTotal.text = data.monthlyCount.total.toString()

            // 오늘 업무 업데이트
            tvTodayPickup.text = data.todayCount.pickup.toString()
            tvTodayDelivery.text = data.todayCount.delivery.toString()

            // 적립 포인트 업데이트
            tvPoints.text = data.points.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
