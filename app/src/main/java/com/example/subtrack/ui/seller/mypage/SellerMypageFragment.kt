package com.please.ui.seller.mypage

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.please.databinding.DialogAddressSearchBinding
import com.please.databinding.FragmentSellerMypageBinding
import com.please.data.models.AddressItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerMypageFragment : Fragment() {

    private var _binding: FragmentSellerMypageBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SellerMypageViewModel by viewModels()
    
    // 현재 선택된 주소 정보를 저장하기 위한 변수
    private var selectedAddress: String = ""
    private var selectedDetailAddress: String = ""
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeUiState()
    }
    
    private fun setupClickListeners() {
        // 주소 입력 클릭 시 주소 검색 다이얼로그 표시
        binding.editTextAddress.setOnClickListener {
            showAddressSearchDialog()
        }
        
        // 주소 업데이트 버튼 클릭
        binding.buttonAddressUpdate.setOnClickListener {
            updateAddress()
        }
        
        // 비밀번호 변경 버튼 클릭
        binding.buttonPasswordUpdate.setOnClickListener {
            updatePassword()
        }
    }
    
    private fun updateAddress() {
        val detailAddress = binding.editTextDetailAddress.text.toString()
        
        if (selectedAddress.isEmpty()) {
            showToast("주소를 선택해주세요")
            return
        }
        
        viewModel.updateAddress(selectedAddress, detailAddress, selectedLatitude, selectedLongitude)
    }
    
    private fun updatePassword() {
        val newPassword = binding.editTextNewPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("비밀번호를 입력해주세요")
            return
        }
        
        if (newPassword != confirmPassword) {
            showToast("비밀번호가 일치하지 않습니다")
            return
        }
        
        // 실제 구현에서는 현재 비밀번호도 입력받아야 하지만, 화면 예시에는 없어 임시 처리
        viewModel.updatePassword("123456", newPassword)
    }
    
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // 로딩 상태 처리
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    
                    // 프로필 정보 표시
                    state.sellerProfile?.let { profile ->
                        binding.textViewTitle.text = "${profile.name} 점주님 | ${profile.id}"
                        
                        // 주소 정보 업데이트
                        selectedAddress = profile.address
                        selectedDetailAddress = profile.detailAddress
                        selectedLatitude = profile.latitude
                        selectedLongitude = profile.longitude
                        
                        binding.editTextAddress.setText(profile.address)
                        binding.editTextDetailAddress.setText(profile.detailAddress)
                    }
                    
                    // 메시지 처리
                    state.message?.let {
                        showToast(it)
                        viewModel.clearMessage()
                    }
                    
                    // 에러 처리
                    state.error?.let {
                        showToast(it)
                        viewModel.clearError()
                    }
                }
            }
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    
    private fun showAddressSearchDialog() {
        val dialogBinding = DialogAddressSearchBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
        
        val addressAdapter = AddressAdapter { addressItem ->
            // 선택한 주소 정보 저장
            selectedAddress = addressItem.address
            selectedLatitude = addressItem.latitude
            selectedLongitude = addressItem.longitude
            
            // UI 업데이트
            binding.editTextAddress.setText(addressItem.address)
            
            // 다이얼로그 닫기
            dialog.dismiss()
        }
        
        dialogBinding.recyclerViewAddressResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
        
        // 검색 버튼 클릭 시 (실제 구현에서는 Kakao 주소 API 등을 사용해야 함)
        dialogBinding.buttonSearch.setOnClickListener {
            val query = dialogBinding.editTextSearchAddress.text.toString()
            if (query.isNotEmpty()) {
                // 임시 테스트 데이터로 주소 표시
                val dummyAddresses = getDummyAddresses()
                addressAdapter.submitList(dummyAddresses)
            } else {
                showToast("검색어를 입력해주세요")
            }
        }
        
        dialog.show()
    }
    
    private fun getDummyAddresses(): List<AddressItem> {
        return listOf(
            AddressItem(
                "서울특별시 강남구 테헤란로 123", 
                "삼성동", 
                37.5012743, 
                127.039585
            ),
            AddressItem(
                "서울특별시 강남구 테헤란로 456", 
                "역삼동", 
                37.5022743, 
                127.0388585
            ),
            AddressItem(
                "서울특별시 강남구 테헤란로 789", 
                "대치동", 
                37.5032743, 
                127.0375585
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
