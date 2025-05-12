package com.please.ui.driver.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverHomeViewModel @Inject constructor() : ViewModel() {

    // UI 상태를 관리하는 StateFlow
    private val _uiState = MutableStateFlow(DriverHomeUiState())
    val uiState: StateFlow<DriverHomeUiState> = _uiState.asStateFlow()

    init {
        loadDriverHomeData()
    }

    private fun loadDriverHomeData() {
        viewModelScope.launch {
            // 실제 API 호출 없이 임시 데이터 설정
            _uiState.value = DriverHomeUiState(
                isLoading = false,
                region = "서울시 강남구 역삼동",
                monthlyPickupCount = 12,
                monthlyDeliveryCount = 8,
                monthlyTotalCount = 20,
                todayPickupCount = 3,
                todayDeliveryCount = 2,
                points = 300,
                error = null
            )
        }
    }
}

// UI 상태를 나타내는 데이터 클래스
data class DriverHomeUiState(
    val isLoading: Boolean = true,
    val region: String = "",
    val monthlyPickupCount: Int = 0,
    val monthlyDeliveryCount: Int = 0,
    val monthlyTotalCount: Int = 0,
    val todayPickupCount: Int = 0,
    val todayDeliveryCount: Int = 0,
    val points: Int = 0,
    val error: String? = null
)
