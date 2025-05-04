package com.please.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.auth.RegisterRequest
import com.please.data.models.auth.UserType
import com.please.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    private val _idCheckState = MutableLiveData<IdCheckState>()
    val idCheckState: LiveData<IdCheckState> = _idCheckState
    
    private val _selectedUserType = MutableLiveData<UserType>(UserType.SELLER)
    val selectedUserType: LiveData<UserType> = _selectedUserType

    fun setUserType(userType: UserType) {
        _selectedUserType.value = userType
    }
    
    fun checkIdDuplicate(id: String) {
        if (id.isEmpty()) {
            _idCheckState.value = IdCheckState.Error("아이디를 입력해주세요.")
            return
        }
        
        _idCheckState.value = IdCheckState.Loading
        
        viewModelScope.launch {
            try {
                val response = repository.checkIdDuplicate(id)
                if (response.isSuccessful) {
                    val isDuplicate = response.body() ?: true
                    if (isDuplicate) {
                        _idCheckState.value = IdCheckState.Duplicate
                    } else {
                        _idCheckState.value = IdCheckState.Available
                    }
                } else {
                    _idCheckState.value = IdCheckState.Error("중복 확인에 실패했습니다: ${response.message()}")
                }
            } catch (e: Exception) {
                _idCheckState.value = IdCheckState.Error("중복 확인에 실패했습니다: ${e.message}")
            }
        }
    }
    
    fun register(
        id: String,
        password: String,
        passwordConfirm: String,
        businessNumber: String? = null,
        address: String? = null,
        city: String? = null,
        district: String? = null,
        transportLicenseFile: String? = null,
        drivingExperienceFile: String? = null
    ) {
        // 입력 검증
        if (id.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            _registerState.value = RegisterState.Error("필수 항목을 모두 입력해주세요.")
            return
        }
        
        // 비밀번호 유효성 검사
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
        if (!passwordRegex.matches(password)) {
            _registerState.value = RegisterState.Error("비밀번호는 최소 8자 이상이며, 영문자와 숫자를 모두 포함해야 합니다.")
            return
        }
        
        if (password != passwordConfirm) {
            _registerState.value = RegisterState.Error("비밀번호가 일치하지 않습니다.")
            return
        }
        
        // 사용자 유형별 필수 항목 검증
        val userType = selectedUserType.value!!
        when (userType) {
            UserType.SELLER -> {
                if (businessNumber.isNullOrEmpty() || address.isNullOrEmpty()) {
                    _registerState.value = RegisterState.Error("사업자 정보를 모두 입력해주세요.")
                    return
                }
            }
            UserType.DRIVER -> {
                if (transportLicenseFile.isNullOrEmpty() || drivingExperienceFile.isNullOrEmpty()) {
                    _registerState.value = RegisterState.Error("필수 서류를 첨부해주세요.")
                    return
                }
            }
        }
        
        _registerState.value = RegisterState.Loading
        
        // 회원가입 요청
        val registerRequest = RegisterRequest(
            id = id,
            password = password,
            passwordConfirm = passwordConfirm,
            userType = userType,
            businessNumber = businessNumber,
            address = address,
            city = city,
            district = district,
            transportLicenseFile = transportLicenseFile,
            drivingExperienceFile = drivingExperienceFile
        )
        
        viewModelScope.launch {
            try {
                val response = repository.register(registerRequest)
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    if (registerResponse.success) {
                        _registerState.value = RegisterState.Success(registerResponse.message)
                    } else {
                        _registerState.value = RegisterState.Error(registerResponse.message)
                    }
                } else {
                    _registerState.value = RegisterState.Error("회원가입에 실패했습니다: ${response.message()}")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("회원가입에 실패했습니다: ${e.message}")
            }
        }
    }

    sealed class RegisterState {
        object Loading : RegisterState()
        data class Success(val message: String) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
    
    sealed class IdCheckState {
        object Loading : IdCheckState()
        object Available : IdCheckState()
        object Duplicate : IdCheckState()
        data class Error(val message: String) : IdCheckState()
    }
}
