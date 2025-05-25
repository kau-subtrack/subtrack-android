package com.please.ui.driver.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.driver.DriverHomeResponse
import com.please.data.repositories.AuthRepository
import com.please.data.repositories.DriverRepository
import com.please.di.BASE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
        if(token == "none") {
            Log.e("DRIVER_HOME/CALL/ERROR", "토큰이 없습니다. 로그인 필요.")
            _homeInfoState.value = HomeInfoState.Error("인증 토큰이 없습니다. 다시 로그인해주세요.")
            return
        }

        Log.d("DRIVER_HOME/TOKEN", "사용 토큰: $token")
        _homeInfoState.value = HomeInfoState.Loading

        viewModelScope.launch {
            Log.d("DRIVER_HOME/CALL", "API 호출 시작: ${BASE_URL}driver/home")
            try {
                // API 호출
                val response = repository.getDriverHome(token)
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("DRIVER_HOME/RESPONSE", "응답 성공: $responseBody")
                    
                    if (responseBody != null) {
                        _homeInfoState.value = HomeInfoState.Success(responseBody)
                    } else {
                        Log.e("DRIVER_HOME/RESPONSE", "응답 본문이 null입니다.")
                        _homeInfoState.value = HomeInfoState.Error("서버에서 빈 응답이 반환되었습니다.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("DRIVER_HOME/ERROR", "HTTP 오류: ${response.code()}, 메시지: ${response.message()}, 본문: $errorBody")
                    
                    val errorMessage = when (response.code()) {
                        401 -> "인증에 실패했습니다. 다시 로그인해주세요."
                        404 -> "등록된 기사 정보가 없습니다."
                        else -> "홈화면 조회에 실패했습니다: ${response.message()} (${response.code()})"
                    }
                    _homeInfoState.value = HomeInfoState.Error(errorMessage)
                }
            } catch (e: HttpException) {
                Log.e("DRIVER_HOME/HTTP_EXCEPTION", "HTTP 예외: ${e.code()}, 메시지: ${e.message()}", e)
                _homeInfoState.value = HomeInfoState.Error("네트워크 오류: ${e.message()}")
            } catch (e: Exception) {
                Log.e("DRIVER_HOME/EXCEPTION", "예외 발생: ${e.javaClass.simpleName}, 메시지: ${e.message}", e)
                _homeInfoState.value = HomeInfoState.Error("오류가 발생했습니다: ${e.message}")
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
