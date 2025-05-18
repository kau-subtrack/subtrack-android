package com.please.ui.seller.subscription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.seller.PlanId
import com.please.data.repositories.AuthRepository
import com.please.data.repositories.SubscriptionRepository
import com.please.ui.auth.register.RegisterViewModel.RegisterState
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

        val token = AuthRepository.AppState.userToken ?: "none"
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val response = repository.registerShipment(token, PlanId(selectedPlanId))
                if (response.isSuccessful && response.body() != null) {
                    val subscriptionResponse = response.body()
                    Log.d("Subscribe/ADD", subscriptionResponse.toString())
                    Log.d("Subscribe/ADD", response.body().toString())

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = "구독이 성공적으로 신청되었습니다.",
                            isSuccess = true
                        )
                    }

                } else {
                    Log.d("Subscribe/ERROR" , "구독에 실패하였습니다.")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "구독 신청에 실패했습니다.",
                        isSuccess = false
                    )
                }
                Log.d("Subscribe/ERROR2" , e.message.toString())
            }

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
