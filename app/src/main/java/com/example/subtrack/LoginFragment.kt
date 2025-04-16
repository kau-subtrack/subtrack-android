package com.example.subtrack

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.subtrack.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var selectedType: String = "자영업자"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullText = "아직 회원이 아니신가요? 회원가입 하기"
        val spannableString = SpannableString(fullText)
        val clickablePart = "회원가입 하기"

        val start = fullText.indexOf(clickablePart)
        val end = start + clickablePart.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // todo: 회원가입 페이지
            }
        }

        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#000000")),
            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvJoin.text = spannableString
        binding.tvJoin.movementMethod = LinkMovementMethod.getInstance()
        binding.tvJoin.highlightColor = Color.TRANSPARENT

        updateUserTypeUI()

        binding.btnOwner.setOnClickListener {
            selectedType = "자영업자"
            updateUserTypeUI()
        }

        binding.btnCourier.setOnClickListener {
            selectedType = "배송기사"
            updateUserTypeUI()
        }

        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "$selectedType 로그인 시도", Toast.LENGTH_SHORT).show()
        }

        binding.tvFindPassword.setOnClickListener {
            // TODO: 비밀번호 찾기 화면 이동
        }
    }

    private fun updateUserTypeUI() {
        if (selectedType == "자영업자") {
            binding.btnOwner.setBackgroundResource(R.drawable.button_selected)
            binding.btnOwner.setTextColor(Color.BLACK)
            binding.btnCourier.setBackgroundResource(R.drawable.button_unselected)
            binding.btnCourier.setTextColor(Color.WHITE)
        } else {
            binding.btnOwner.setBackgroundResource(R.drawable.button_unselected)
            binding.btnOwner.setTextColor(Color.WHITE)
            binding.btnCourier.setBackgroundResource(R.drawable.button_selected)
            binding.btnCourier.setTextColor(Color.BLACK)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
