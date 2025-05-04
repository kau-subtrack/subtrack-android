package com.please.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.please.R
import com.please.data.models.auth.UserType
import com.please.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    // Show loading indicator
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is LoginViewModel.LoginState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    
                    // Navigate based on user type
                    when (state.data.user.userType) {
                        UserType.SELLER -> {
                            findNavController().navigate(R.id.action_loginFragment_to_sellerNavGraph)
                        }
                        UserType.DRIVER -> {
                            findNavController().navigate(R.id.action_loginFragment_to_driverNavGraph)
                        }
                    }
                }
                is LoginViewModel.LoginState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        viewModel.selectedUserType.observe(viewLifecycleOwner) { userType ->
            updateUserTypeSelection(userType)
        }
    }

    private fun setupListeners() {
        // User type selection
        binding.btnSeller.setOnClickListener {
            viewModel.setUserType(UserType.SELLER)
        }
        
        binding.btnDriver.setOnClickListener {
            viewModel.setUserType(UserType.DRIVER)
        }
        
        // Login button
        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(id, password)
        }
        
        // Find ID/Password
        binding.tvFindIdPassword.setOnClickListener {
            // 추후 구현 예정
            Toast.makeText(requireContext(), "아이디/비밀번호 찾기는 추후 구현 예정입니다.", Toast.LENGTH_SHORT).show()
        }
        
        // Register
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        
        // Remember ID checkbox
        binding.cbRememberId.setOnCheckedChangeListener { _, isChecked ->
            // 아이디 저장 로직 추후 구현
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
        binding.cbRememberId.isChecked = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
