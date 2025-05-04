package com.please.ui.seller.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.SellerProfile
import com.please.data.repositories.SellerProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerMypageViewModel @Inject constructor(
    private val repository: SellerProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MypageUiState())
    val uiState: StateFlow<MypageUiState> = _uiState.asStateFlow()

    init {
        loadSellerProfile()
    }

    fun loadSellerProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getSellerProfile().fold(
                onSuccess = { profile ->
                    _uiState.update { 
                        it.copy(
                            sellerProfile = profile,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "프로필 정보를 가져오는데 실패했습니다."
                        )
                    }
                }
            )
        }
    }

    fun updateAddress(address: String, detailAddress: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.updateAddress(address, detailAddress, latitude, longitude).fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            message = "주소가 성공적으로 업데이트되었습니다.",
                            error = null
                        )
                    }
                    // 업데이트 후 프로필 다시 로드
                    loadSellerProfile()
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "주소 업데이트에 실패했습니다."
                        )
                    }
                }
            )
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.updatePassword(currentPassword, newPassword).fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            message = "비밀번호가 성공적으로 변경되었습니다.",
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "비밀번호 변경에 실패했습니다."
                        )
                    }
                }
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class MypageUiState(
    val sellerProfile: SellerProfile? = null,
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
