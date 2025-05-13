package com.please.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.auth.LoginResponse
import com.please.data.models.auth.User
import com.please.data.models.auth.UserType
import com.please.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel

class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _selectedUserType = MutableLiveData<UserType>(UserType.OWNER)
    val selectedUserType: LiveData<UserType> = _selectedUserType

    fun setUserType(userType: UserType) {
        _selectedUserType.value = userType
    }

    fun login(id: String, password: String) {
        if (id.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("아이디와 비밀번호를 입력해주세요.")
            return
        }

        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            Log.d("LOGIN/TRY" , id)
            try {
                //login 시도.
                val response = repository.login(id, password, selectedUserType.value!!)
                Log.d("LOGIN/TRY" , id)
                Log.d("LOGIN/TRY" , response.body().toString())

                //성공시, res body반환.
                if (response.isSuccessful && response.body() != null) {
                    _loginState.value = LoginState.Success(response.body()!!)
                } else {
                    _loginState.value = LoginState.Error("로그인에 실패했습니다: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.d("LOGIN/TRY" , e.message.toString())
                _loginState.value = LoginState.Error("로그인에 실패했습니다: ${e.message}")
            }
        }
    }

/*
    fun login(id: String, password: String) {
        if (id.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("아이디와 비밀번호를 입력해주세요.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            kotlinx.coroutines.delay(1000L)  // 잠시 로딩 흉내

            if (id == "testuser" && password == "password123") {
                val dummyUser = User(
                    id = "1",
                    name = "테스트 사용자",
                    userType = selectedUserType.value ?: UserType.OWNER,
                    email = "test@example.com",
                    password = "password123"
                )
                val dummyResponse = LoginResponse(
                    token = "dummy-token-abc123",
                    //id = "1",
                    message = "로그인 성공"
                )
                _loginState.value = LoginState.Success(dummyResponse)
            } else {
                _loginState.value = LoginState.Error("아이디 또는 비밀번호가 올바르지 않습니다.")
            }
        }
    }*/

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val data: LoginResponse) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
