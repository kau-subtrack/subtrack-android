package com.please.ui.seller.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.please.R
import com.please.data.repositories.AuthRepository
import com.please.databinding.FragmentSellerManualBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SellerManualFragment : Fragment() {

    private var _binding: FragmentSellerManualBinding? = null
    private val binding get() = _binding!!
    
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerManualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 로그아웃 버튼 클릭 리스너 설정
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
        findNavController().navigate(R.id.action_sellerManualFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}