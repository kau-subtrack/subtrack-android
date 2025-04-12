package com.example.subtrack

import android.os.Bundle
import android.graphics.Color
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.subtrack.databinding.ActivityLoginBinding
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var selectedType: String = "자영업자"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fullText = "아직 회원이 아니신가요? 회원가입 하기"
        val spannableString = SpannableString(fullText)
        val clickablePart = "회원가입 하기"

        // 클릭 가능한 영역 설정
        val start = fullText.indexOf(clickablePart)
        val end = start + clickablePart.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

        // 텍스트 꾸미기
        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#000000")),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 적용
        binding.tvJoin.text = spannableString
        binding.tvJoin.movementMethod = LinkMovementMethod.getInstance()
        binding.tvJoin.highlightColor = Color.TRANSPARENT

        // 기본 선택 설정: 자영업자
        updateUserTypeUI()

        binding.btnOwner.setOnClickListener {
            selectedType = "자영업자"
            updateUserTypeUI()
        }

        binding.btnCourier.setOnClickListener {
            selectedType = "배송기사"
            updateUserTypeUI()
        }

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val password = binding.etPassword.text.toString()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 로그인 로직 (API 연동 등)
            Toast.makeText(this, "$selectedType 로 로그인 시도", Toast.LENGTH_SHORT).show()
        }


        // 회원가입 버튼
        binding.tvJoin.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기
        binding.tvFindPassword.setOnClickListener {
            // TODO: 비밀번호 찾기 화면 이동
        }

        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val pw = binding.etPassword.text.toString()
            Toast.makeText(this, "$selectedType 로그인 시도", Toast.LENGTH_SHORT).show()
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
}
