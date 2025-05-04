package com.please.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.please.R
import com.please.data.models.auth.UserType
import com.please.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterViewModel.RegisterState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is RegisterViewModel.RegisterState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is RegisterViewModel.RegisterState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        viewModel.idCheckState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterViewModel.IdCheckState.Loading -> {
                    binding.tvIdStatus.visibility = View.VISIBLE
                    binding.tvIdStatus.text = "확인 중..."
                    binding.tvIdStatus.setTextColor(resources.getColor(R.color.black, null))
                    binding.btnCheckId.isEnabled = false
                }
                is RegisterViewModel.IdCheckState.Available -> {
                    binding.tvIdStatus.visibility = View.VISIBLE
                    binding.tvIdStatus.text = "사용 가능한 아이디입니다."
                    binding.tvIdStatus.setTextColor(resources.getColor(R.color.green, null))
                    binding.btnCheckId.isEnabled = true
                }
                is RegisterViewModel.IdCheckState.Duplicate -> {
                    binding.tvIdStatus.visibility = View.VISIBLE
                    binding.tvIdStatus.text = "이미 사용중인 아이디입니다."
                    binding.tvIdStatus.setTextColor(resources.getColor(R.color.red, null))
                    binding.btnCheckId.isEnabled = true
                }
                is RegisterViewModel.IdCheckState.Error -> {
                    binding.tvIdStatus.visibility = View.VISIBLE
                    binding.tvIdStatus.text = state.message
                    binding.tvIdStatus.setTextColor(resources.getColor(R.color.red, null))
                    binding.btnCheckId.isEnabled = true
                }
            }
        }
        
        viewModel.selectedUserType.observe(viewLifecycleOwner) { userType ->
            updateUserTypeSelection(userType)
            updateVisibilityBasedOnUserType(userType)
        }
    }
    
    private fun setupListeners() {
        // 사용자 유형 선택
        binding.btnSeller.setOnClickListener {
            viewModel.setUserType(UserType.SELLER)
        }
        
        binding.btnDriver.setOnClickListener {
            viewModel.setUserType(UserType.DRIVER)
        }
        
        // 아이디 중복 확인
        binding.btnCheckId.setOnClickListener {
            viewModel.checkIdDuplicate(binding.etId.text.toString())
        }
        
        // 회원가입 버튼
        binding.btnRegister.setOnClickListener {
            val id = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()
            val passwordConfirm = binding.etPasswordConfirm.text.toString()
            
            // 사용자 유형별 추가 정보
            when (viewModel.selectedUserType.value) {
                UserType.SELLER -> {
                    val businessNumber = binding.etBusinessNumber.text.toString()
                    val address = binding.etAddress.text.toString()
                    
                    viewModel.register(
                        id = id,
                        password = password,
                        passwordConfirm = passwordConfirm,
                        businessNumber = businessNumber,
                        address = address
                    )
                }
                UserType.DRIVER -> {
                    // 실제 구현에서는 파일 업로드 로직이 필요하지만, 지금은 더미 데이터 사용
                    val transportLicenseFile = "dummy_transport_license_file"
                    val drivingExperienceFile = "dummy_driving_experience_file"
                    val city = binding.spinnerCity.selectedItem?.toString() ?: ""
                    val district = binding.spinnerDistrict.selectedItem?.toString() ?: ""
                    
                    viewModel.register(
                        id = id,
                        password = password,
                        passwordConfirm = passwordConfirm,
                        city = city,
                        district = district,
                        transportLicenseFile = transportLicenseFile,
                        drivingExperienceFile = drivingExperienceFile
                    )
                }
                else -> {
                    Toast.makeText(requireContext(), "유형을 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 비밀번호 확인
        binding.etPassword.addTextChangedListener { updatePasswordMatchStatus() }
        binding.etPasswordConfirm.addTextChangedListener { updatePasswordMatchStatus() }
    }

    private fun updatePasswordMatchStatus() {
        val pw = binding.etPassword.text.toString()
        val confirm = binding.etPasswordConfirm.text.toString()

        // 비밀번호 유효성 검사
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
        if (pw.isNotEmpty()) {
            if (!passwordRegex.matches(pw)) {
                binding.tvPasswordMatch.visibility = View.VISIBLE
                binding.tvPasswordMatch.text = "비밀번호는 최소 8자 이상이며, 영문자와 숫자를 모두 포함해야 합니다."
                binding.tvPasswordMatch.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                return
            }
        }

        // 비밀번호 일치 여부 검사
        if (pw.isNotEmpty() && confirm.isNotEmpty()) {
            if (pw == confirm) {
                binding.tvPasswordMatch.visibility = View.VISIBLE
                binding.tvPasswordMatch.text = "비밀번호가 일치합니다."
                binding.tvPasswordMatch.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            } else {
                binding.tvPasswordMatch.visibility = View.VISIBLE
                binding.tvPasswordMatch.text = "비밀번호가 일치하지 않습니다."
                binding.tvPasswordMatch.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
        } else {
            binding.tvPasswordMatch.visibility = View.GONE
        }
    }

    private fun updateUserTypeSelection(userType: UserType) {
        when (userType) {
            UserType.SELLER -> {
                binding.btnSeller.setBackgroundResource(R.drawable.bg_button_selected)
                binding.btnSeller.setTextColor(resources.getColor(R.color.white, null))
                
                binding.btnDriver.setBackgroundResource(R.drawable.bg_button_unselected)
                binding.btnDriver.setTextColor(resources.getColor(R.color.black, null))
            }
            UserType.DRIVER -> {
                binding.btnDriver.setBackgroundResource(R.drawable.bg_button_selected)
                binding.btnDriver.setTextColor(resources.getColor(R.color.white, null))
                
                binding.btnSeller.setBackgroundResource(R.drawable.bg_button_unselected)
                binding.btnSeller.setTextColor(resources.getColor(R.color.black, null))
            }
        }

        // 사용자 유형 변경 시 입력값 초기화
        binding.etId.text?.clear()
        binding.etPassword.text?.clear()
        binding.etPasswordConfirm.text?.clear()
        binding.tvIdStatus.visibility = View.GONE
    }
    
    private fun updateVisibilityBasedOnUserType(userType: UserType) {
        when (userType) {
            UserType.SELLER -> {
                // 자영업자 관련 항목 표시
                binding.sellerInfoLayout.visibility = View.VISIBLE
                binding.driverInfoLayout.visibility = View.GONE
            }
            UserType.DRIVER -> {
                // 배송기사 관련 항목 표시
                binding.sellerInfoLayout.visibility = View.GONE
                binding.driverInfoLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
