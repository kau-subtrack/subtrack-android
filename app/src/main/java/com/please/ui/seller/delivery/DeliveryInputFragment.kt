package com.please.ui.seller.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.please.R
import com.please.data.models.seller.PackageSize
import com.please.databinding.FragmentDeliveryInputBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class DeliveryInputFragment : Fragment() {

    private var _binding: FragmentDeliveryInputBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DeliveryInputViewModel by viewModels()
    
    // SafeArgs로 전달받은 인자
    private val args: DeliveryInputFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // SafeArgs로 전달받은 날짜 설정
        val pickupDate = Date(args.pickupDate)
        viewModel.setPickupDate(pickupDate)
        
        setupListeners()
        setupObservers()
    }
    
    private fun setupListeners() {
        // 택배 크기 선택
        binding.btnSmall.setOnClickListener { selectPackageSize(PackageSize.SMALL) }
        binding.btnMedium.setOnClickListener { selectPackageSize(PackageSize.MEDIUM) }
        binding.btnLarge.setOnClickListener { selectPackageSize(PackageSize.LARGE) }
        binding.btnXLarge.setOnClickListener { selectPackageSize(PackageSize.XLARGE) }
        
        // 취급주의 품목 토글
        binding.btnCaution.setOnClickListener {
            viewModel.toggleCautionRequired()
        }
        
        // 주소 검색 버튼
        binding.btnAddressSearch.setOnClickListener {
            // 실제 구현에서는 주소 검색 API 호출
            // 데모를 위해 임의의 주소 설정
            viewModel.setAddress("서울시 강남구 테헤란로 123")
        }
        
        // 저장 버튼
        binding.btnSave.setOnClickListener {
            // 입력된 값을 ViewModel에 전달
            viewModel.setProductName(binding.etProductName.text.toString())
            viewModel.setRecipientName(binding.etRecipientName.text.toString())
            viewModel.setRecipientPhone(binding.etRecipientPhone.text.toString())
            
            // 상세 주소 설정
            if (binding.etDetailAddress.text.isNotEmpty()) {
                viewModel.setDetailAddress(binding.etDetailAddress.text.toString())
            }
            
            // 배송 정보 저장
            viewModel.saveDeliveryInfo()
        }
        
        // 취소 버튼
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupObservers() {
        // 택배 크기 관찰
        viewModel.selectedPackageSize.observe(viewLifecycleOwner) { size ->
            updatePackageSizeUI(size)
        }
        
        // 취급주의 여부 관찰
        viewModel.isCautionRequired.observe(viewLifecycleOwner) { isRequired ->
            binding.btnCaution.isSelected = isRequired
            if (isRequired) {
                binding.btnCaution.setBackgroundResource(R.drawable.bg_button_selected)
                binding.btnCaution.setTextColor(resources.getColor(R.color.white, null))
            } else {
                binding.btnCaution.setBackgroundResource(R.drawable.bg_button_unselected)
                binding.btnCaution.setTextColor(resources.getColor(R.color.black, null))
            }
        }
        
        // 주소 검색 완료 관찰
        viewModel.addressSearchComplete.observe(viewLifecycleOwner) { isComplete ->
            binding.etAddress.setText(viewModel.address.value ?: "")
        }
        
        // 저장 결과 관찰
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DeliveryInputViewModel.SaveResult.Success -> {
                    Toast.makeText(requireContext(), "배송 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is DeliveryInputViewModel.SaveResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun selectPackageSize(size: PackageSize) {
        viewModel.setPackageSize(size)
    }
    
    private fun updatePackageSizeUI(size: PackageSize) {
        // 모든 버튼을 기본 상태로 초기화
        binding.btnSmall.setBackgroundResource(R.drawable.bg_button_unselected)
        binding.btnSmall.setTextColor(resources.getColor(R.color.black, null))
        
        binding.btnMedium.setBackgroundResource(R.drawable.bg_button_unselected)
        binding.btnMedium.setTextColor(resources.getColor(R.color.black, null))
        
        binding.btnLarge.setBackgroundResource(R.drawable.bg_button_unselected)
        binding.btnLarge.setTextColor(resources.getColor(R.color.black, null))
        
        binding.btnXLarge.setBackgroundResource(R.drawable.bg_button_unselected)
        binding.btnXLarge.setTextColor(resources.getColor(R.color.black, null))
        
        // 선택된 버튼만 강조 표시
        when (size) {
            PackageSize.SMALL -> {
                binding.btnSmall.setBackgroundResource(R.drawable.bg_button_selected)
                binding.btnSmall.setTextColor(resources.getColor(R.color.white, null))
            }
            PackageSize.MEDIUM -> {
                binding.btnMedium.setBackgroundResource(R.drawable.bg_button_selected)
                binding.btnMedium.setTextColor(resources.getColor(R.color.white, null))
            }
            PackageSize.LARGE -> {
                binding.btnLarge.setBackgroundResource(R.drawable.bg_button_selected)
                binding.btnLarge.setTextColor(resources.getColor(R.color.white, null))
            }
            PackageSize.XLARGE -> {
                binding.btnXLarge.setBackgroundResource(R.drawable.bg_button_selected)
                binding.btnXLarge.setTextColor(resources.getColor(R.color.white, null))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
