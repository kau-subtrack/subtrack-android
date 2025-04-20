package com.example.subtrack.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.subtrack.MainActivity
import com.example.subtrack.R
import com.example.subtrack.databinding.FragmentRegisterBinding
import com.example.subtrack.util.setUserTypeUI

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var selectedUserType: String = USER_TYPE_OWNER

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        (activity as MainActivity).hideBottomNavigation(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        binding.btnOwner.setOnClickListener {
            selectedUserType = USER_TYPE_OWNER
            updateUI()
        }

        binding.btnCourier.setOnClickListener {
            selectedUserType = USER_TYPE_COURIER
            updateUI()
        }

        binding.btnCheckId.setOnClickListener {
            // TODO: 아이디 중복 확인 API 연동
        }

        binding.btnRegister.setOnClickListener {
            val id = binding.etId.text.toString()
            val pw = binding.etPassword.text.toString()
            val confirmPw = binding.etConfirmPassword.text.toString()

            // 필수 항목 유효성 검사 예시
            if (id.isBlank() || pw.isBlank() || confirmPw.isBlank()) {
                Toast.makeText(requireContext(), "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pw != confirmPw) {
                Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 서버에 회원가입 요청 보내기 (성공 가정)
            Toast.makeText(requireContext(), "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show()

            // 로그인 화면으로 이동
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

    }

    private fun updateUI() {
        val isOwner = selectedUserType == USER_TYPE_OWNER

        setUserTypeUI(
            isOwner,
            binding.btnOwner,
            binding.btnCourier
        )

        binding.etShopAddress.visibility = if (isOwner) View.VISIBLE else View.GONE
        binding.etBizNum.visibility = if (isOwner) View.VISIBLE else View.GONE

        binding.btnUploadLicense.visibility = if (isOwner) View.GONE else View.VISIBLE
        binding.btnUploadCareer.visibility = if (isOwner) View.GONE else View.VISIBLE
        binding.layoutRegion.visibility = if (isOwner) View.GONE else View.VISIBLE
        binding.tvCourierNotice.visibility = if (isOwner) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val USER_TYPE_OWNER = "OWNER"
        const val USER_TYPE_COURIER = "COURIER"
    }
}

