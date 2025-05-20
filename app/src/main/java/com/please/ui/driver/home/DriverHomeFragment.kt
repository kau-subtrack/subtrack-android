package com.please.ui.driver.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.please.R
import com.please.data.repositories.AuthRepository
import com.please.databinding.FragmentDriverHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DriverHomeFragment : Fragment() {

    private var _binding: FragmentDriverHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DriverHomeViewModel by viewModels()
    
    @Inject
    lateinit var authRepository: AuthRepository

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
        setupLogoutButton()
    }
    
    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }
    
    private fun logout() {
        // 로그아웃 처리: 토큰 제거
        authRepository.logout()
        
        // 로그인 화면으로 이동
        findNavController().navigate(R.id.action_driverHomeFragment_to_loginFragment)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: DriverHomeUiState) {
        binding.apply {
            // 담당 위치 업데이트
            tvLocation.text = state.region

            // 이번 달 수행 건수 업데이트
            tvMonthlyPickup.text = state.monthlyPickupCount.toString()
            tvMonthlyDelivery.text = state.monthlyDeliveryCount.toString()
            tvMonthlyTotal.text = state.monthlyTotalCount.toString()

            // 오늘 업무 업데이트
            tvTodayPickup.text = state.todayPickupCount.toString()
            tvTodayDelivery.text = state.todayDeliveryCount.toString()

            // 적립 포인트 업데이트
            tvPoints.text = state.points.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
