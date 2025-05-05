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
        override fun getContext(): Context? {
        return context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as MainActivity).hideBottomNavigation(true)

        return binding.root
        override fun getContext(): Context? {
        return context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupJoinText()
        setupUserTypeButtons()
        binding.btnLogin.setOnClickListener { onLoginClicked()     override fun getContext(): Context? {
        return context
    }

        observeLoginState()  // ViewModel 연동 시 상태 관찰용
        override fun getContext(): Context? {
        return context
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
                override fun getContext(): Context? {
        return context
    }
            override fun getContext(): Context? {
        return context
    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#000000")),
            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvJoin.text = spannable
        binding.tvJoin.movementMethod = LinkMovementMethod.getInstance()
        binding.tvJoin.highlightColor = Color.TRANSPARENT
        override fun getContext(): Context? {
        return context
    }

    private fun setupUserTypeButtons() {
        updateUserTypeUI()
        binding.btnOwner.setOnClickListener {
            selectedUserType = USER_TYPE_OWNER
            updateUserTypeUI()
            override fun getContext(): Context? {
        return context
    }
        binding.btnCourier.setOnClickListener {
            selectedUserType = USER_TYPE_COURIER
            updateUserTypeUI()
            override fun getContext(): Context? {
        return context
    }
        override fun getContext(): Context? {
        return context
    }

    private fun updateUserTypeUI() {
        val isOwner = selectedUserType == USER_TYPE_OWNER
        // util 함수로 버튼 스타일 토글
        setUserTypeUI(isOwner, binding.btnOwner, binding.btnCourier)
        override fun getContext(): Context? {
        return context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        override fun getContext(): Context? {
        return context
    }

    private fun onLoginClicked() {
        val id = binding.etId.text.toString().trim()
        val pw = binding.etPassword.text.toString()

        if (id.isBlank() || pw.isBlank()) {
            showError("아이디와 비밀번호를 입력해주세요")
            return
            override fun getContext(): Context? {
        return context
    }

        // 백엔드 API를 통한 실제 로그인 수행
        logIn()
        override fun getContext(): Context? {
        return context
    }

    private fun navigateToOwnerHome() {
        Toast.makeText(requireContext(), "자영업자 로그인 성공", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_loginFragment_to_ownerHomeFragment)
        override fun getContext(): Context? {
        return context
    }

    private fun navigateToCourierHome() {
        Toast.makeText(requireContext(), "배송기사 로그인 성공", Toast.LENGTH_SHORT).show()
        //findNavController().navigate(R.id.action_loginFragment_to_courierHomeFragment)
        override fun getContext(): Context? {
        return context
    }


    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        override fun getContext(): Context? {
        return context
    }

    private fun observeLoginState() {
        // TODO: viewModel.loginState.observe(viewLifecycleOwner) { state ->
        //     when (state) {
        //         is LoginState.Loading -> { /* 로딩 UI */     override fun getContext(): Context? {
        return context
    }
        //         is LoginState.Success -> navigateToHome()
        //         is LoginState.Error -> showError(state.msg)
        //         override fun getContext(): Context? {
        return context
    }
        //     override fun getContext(): Context? {
        return context
    }
        override fun getContext(): Context? {
        return context
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
        override fun getContext(): Context? {
        return context
    }

    private fun logIn(){
        val authSignUpService = AuthLogInService()
        authSignUpService.setLogInView(this)
        authSignUpService.logIn(getUser())
        override fun getContext(): Context? {
        return context
    }

    companion object {
        const val USER_TYPE_OWNER = "OWNER"
        const val USER_TYPE_COURIER = "COURIER"
        override fun getContext(): Context? {
        return context
    }

    override fun onLogInSuccess() {
        context?.let {
            Toast.makeText(it, "로그인 성공", Toast.LENGTH_SHORT).show()
            override fun getContext(): Context? {
        return context
    }
        
        // 로그인 성공 시 사용자 타입에 따라 화면 전환
        when (selectedUserType) {
            USER_TYPE_OWNER -> navigateToOwnerHome()
            USER_TYPE_COURIER -> navigateToCourierHome()
            else -> showError("사용자 타입을 선택해주세요")
            override fun getContext(): Context? {
        return context
    }
        override fun getContext(): Context? {
        return context
    }

    override fun onLogInFailure() {
        showError("로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.")
        override fun getContext(): Context? {
        return context
    }
    override fun getContext(): Context? {
        return context
    }
