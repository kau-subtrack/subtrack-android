package com.please.ui.seller.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.please.data.models.SubscriptionPlan
import com.please.databinding.FragmentSellerSubscriptionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerSubscriptionFragment : Fragment() {

    private var _binding: FragmentSellerSubscriptionBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SubscriptionViewModel by viewModels()
    private var selectedPlanId: Int = 0
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeUiState()
    }
    
    private fun setupClickListeners() {
        // 구독 플랜 카드 클릭 리스너 설정
        binding.cardLite.setOnClickListener { selectPlan(1) }
        binding.cardLitePlus.setOnClickListener { selectPlan(2) }
        binding.cardStandard.setOnClickListener { selectPlan(3) }
        binding.cardStandardPlus.setOnClickListener { selectPlan(4) }
        binding.cardPremium.setOnClickListener { selectPlan(5) }
        binding.cardPremiumPlus.setOnClickListener { selectPlan(6) }
        
        // 구독하기 버튼 클릭 리스너
        binding.buttonSubscribe.setOnClickListener {
            if (selectedPlanId > 0) {
                viewModel.selectPlan(selectedPlanId)
                viewModel.subscribeToPlan()
            } else {
                Toast.makeText(requireContext(), "구독 플랜을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun selectPlan(planId: Int) {
        selectedPlanId = planId
        updatePlanSelection()
    }
    
    private fun updatePlanSelection() {
        // 모든 라디오 버튼 초기화
        binding.radioLite.isChecked = false
        binding.radioLitePlus.isChecked = false
        binding.radioStandard.isChecked = false
        binding.radioStandardPlus.isChecked = false
        binding.radioPremium.isChecked = false
        binding.radioPremiumPlus.isChecked = false
        
        // 선택된 플랜의 라디오 버튼만 체크
        when (selectedPlanId) {
            1 -> binding.radioLite.isChecked = true
            2 -> binding.radioLitePlus.isChecked = true
            3 -> binding.radioStandard.isChecked = true
            4 -> binding.radioStandardPlus.isChecked = true
            5 -> binding.radioPremium.isChecked = true
            6 -> binding.radioPremiumPlus.isChecked = true
        }
    }
    
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    
                    state.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        viewModel.clearMessage()
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
