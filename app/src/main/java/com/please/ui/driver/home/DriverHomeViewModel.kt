package com.please.ui.driver.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.driver.DriverHomeResponse
import com.please.data.repositories.AuthRepository
import com.please.data.repositories.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverHomeViewModel @Inject constructor(
    private val repository: DriverRepository
) : ViewModel() {

    private val _homeInfoState = MutableLiveData<HomeInfoState>()
    val homeInfoState: LiveData<HomeInfoState> = _homeInfoState

    init {
        val token = AuthRepository.AppState.userToken ?: "none"
        loadHomeInfo(token)
    }

    private fun loadHomeInfo(token: String) {
        if(token == "none") Log.d("DRIVER_HOME/CALL/ERROR", "none")

        _homeInfoState.value = HomeInfoState.Loading

        viewModelScope.launch {
            Log.d("DRIVER_HOME/CALL", token)
            try {
                // API 호출
                val response = repository.getDriverHome(token)
                Log.d("DRIVER_HOME/CALL", token)
                Log.d("DRIVER_HOME/CALL", response.body().toString())

                // 성공 시, res body 반환
                if (response.isSuccessful && response.body() != null) {
                    _homeInfoState.value = HomeInfoState.Success(response.body()!!)
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "인증에 실패했습니다. 다시 로그인해주세요."
                        404 -> "등록된 기사 정보가 없습니다."
                        else -> "홈화면 조회에 실패했습니다: ${response.message()} (${response.code()})"
                    }
                    _homeInfoState.value = HomeInfoState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.d("DRIVER_HOME/CALL", e.message.toString())
                _homeInfoState.value = HomeInfoState.Error("홈화면 조회에 실패했습니다: ${e.message}")
            }
        }
    }

    // 데이터 새로고침
    fun refreshData() {
        val token = AuthRepository.AppState.userToken ?: "none"
        loadHomeInfo(token)
    }

    // 홈 정보 상태를 나타내는 sealed class
    sealed class HomeInfoState {
        object Loading : HomeInfoState()
        data class Success(val data: DriverHomeResponse) : HomeInfoState()
        data class Error(val message: String) : HomeInfoState()
    }
}
