package com.example.subtrack.ui.view

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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.subtrack.MainActivity
import com.example.subtrack.R
import com.example.subtrack.data.model.reqUserLogIn
import com.example.subtrack.data.model.reqUserLogIns
import com.example.subtrack.data.model.reqUserSignUp
import com.example.subtrack.databinding.FragmentLoginBinding
import com.example.subtrack.service.AuthLogInService
import com.example.subtrack.service.AuthSignUpService
import com.example.subtrack.service.viewInterface.LogInView
import com.example.subtrack.util.setUserTypeUI

class LoginFragment : Fragment(), LogInView {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // 나중에 ViewModel 주입할 자리
    // private val viewModel: LoginViewModel by viewModels()

    private var selectedUserType: String = USER_TYPE_OWNER

    private val navController by lazy {
        findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as MainActivity).hideBottomNavigation(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupJoinText()
        setupUserTypeButtons()
        binding.btnLogin.setOnClickListener { onLoginClicked() }

        observeLoginState()  // ViewModel 연동 시 상태 관찰용
    }

    private fun setupJoinText() {
        val fullText = "아직 회원이 아니신가요? 회원가입 하기"
        val clickablePart = "회원가입 하기"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(clickablePart)
        val end = start + clickablePart.length

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#000000")),
            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvJoin.text = spannable
        binding.tvJoin.movementMethod = LinkMovementMethod.getInstance()
        binding.tvJoin.highlightColor = Color.TRANSPARENT
    }

    private fun setupUserTypeButtons() {
        updateUserTypeUI()
        binding.btnOwner.setOnClickListener {
            selectedUserType = USER_TYPE_OWNER
            updateUserTypeUI()
        }
        binding.btnCourier.setOnClickListener {
            selectedUserType = USER_TYPE_COURIER
            updateUserTypeUI()
        }
    }

    private fun updateUserTypeUI() {
        val isOwner = selectedUserType == USER_TYPE_OWNER
        // util 함수로 버튼 스타일 토글
        setUserTypeUI(isOwner, binding.btnOwner, binding.btnCourier)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onLoginClicked() {
        val id = binding.etId.text.toString().trim()
        val pw = binding.etPassword.text.toString()

        if (id.isBlank() || pw.isBlank()) {
            showError("아이디와 비밀번호를 입력해주세요")
            return
        }

        // TODO: 나중에 viewModel.login(id, pw) 양식으로 변경 필요. 재모듈화
        logIn()

        // 지금은 선택된 타입에 따라 홈 화면만 분기
        when (selectedUserType) {
            USER_TYPE_OWNER -> navigateToOwnerHome()
            USER_TYPE_COURIER -> navigateToCourierHome()
            else -> showError("사용자 타입을 선택해주세요")
        }
    }

    private fun navigateToOwnerHome() {
        Toast.makeText(requireContext(), "자영업자 로그인 성공", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_loginFragment_to_ownerHomeFragment)
    }

    private fun navigateToCourierHome() {
        Toast.makeText(requireContext(), "배송기사 로그인 성공", Toast.LENGTH_SHORT).show()
        //findNavController().navigate(R.id.action_loginFragment_to_courierHomeFragment)
    }


    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun observeLoginState() {
        // TODO: viewModel.loginState.observe(viewLifecycleOwner) { state ->
        //     when (state) {
        //         is LoginState.Loading -> { /* 로딩 UI */ }
        //         is LoginState.Success -> navigateToHome()
        //         is LoginState.Error -> showError(state.msg)
        //     }
        // }
    }


    private fun getUser(): reqUserLogIn {
        val email: String = binding.etId.text.toString()
        val password: String = binding.etPassword.text.toString()

        return reqUserLogIn(
            email, password, selectedUserType
        )

        /*
        val r = reqUserLogIns()
        r.add(reqUserLogIn(email, password, selectedUserType))
        return r

         */
    }

    private fun logIn(){
        val authSignUpService = AuthLogInService()
        authSignUpService.setLogInView(this)
        authSignUpService.logIn(getUser())
    }

    companion object {
        const val USER_TYPE_OWNER = "OWNER"
        const val USER_TYPE_COURIER = "COURIER"
    }

    override fun onLogInSuccess() {
        context?.let {
            Toast.makeText(it, "로그인 성공", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLogInFailure() {
        TODO("Not yet implemented")
    }
}
