package com.please.ui.seller.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.SubscriptionPlan
import com.please.data.repositories.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()
    
    private var selectedPlanId: Int = 0

    fun selectPlan(planId: Int) {
        selectedPlanId = planId
    }

    fun subscribeToPlan() {
        if (selectedPlanId <= 0) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.subscribeToPlan(selectedPlanId).fold(
                onSuccess = { response ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            message = "구독이 성공적으로 신청되었습니다.",
                            isSuccess = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            message = error.message ?: "구독 신청에 실패했습니다.",
                            isSuccess = false
                        )
                    }
                }
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

data class SubscriptionUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSuccess: Boolean = false
)
