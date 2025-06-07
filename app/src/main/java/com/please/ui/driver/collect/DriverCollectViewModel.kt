package com.please.ui.driver.collect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.api.DriverApiService
import com.please.data.models.driver.IdRequest
import com.please.data.models.driver.PickCompletedResponse
import com.please.data.models.driver.PickList
import com.please.data.models.driver.PickResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DriverCollectViewModel @Inject constructor(
    private val driverApiService: DriverApiService
) : ViewModel() {
    
    // 수거 목록 LiveData
    private val _pickupList = MutableLiveData<List<PickList>>()
    val pickupList: LiveData<List<PickList>> = _pickupList
    
    // 로딩 상태 LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 에러 메시지 LiveData
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 작업 완료 상태 LiveData
    private val _operationCompleted = MutableLiveData<Boolean>()
    val operationCompleted: LiveData<Boolean> = _operationCompleted

    /**
     * 수거 목록을 가져오는 함수
     * 
     * @param token 사용자 인증 토큰
     */
    fun fetchPickupList(token: String) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                android.util.Log.d("API_DEBUG", "수거 목록 API 호출 시작...")
                val response = driverApiService.getPickupList("Bearer $token")
                
                // 응답 로깅
                android.util.Log.d("API_DEBUG", "수거 목록 API 응답 상태 코드: ${response.code()}")
                
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string() ?: "내용 없음"
                    android.util.Log.e("API_ERROR", "에러 응답 본문: $errorBody")
                } else {
                    android.util.Log.d("API_DEBUG", "수거 목록 API 응답 성공")
                }
                
                handlePickupListResponse(response)
            } catch (e: IOException) {
                android.util.Log.e("API_ERROR", "네트워크 연결 오류: ${e.message}", e)
                _errorMessage.value = "네트워크 연결 오류가 발생했습니다."
            } catch (e: Exception) {
                android.util.Log.e("API_ERROR", "API 호출 중 예외 발생: ${e.message}", e)
                _errorMessage.value = "오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 수거 목록 API 응답을 처리하는 함수
     * 
     * @param response API 응답
     */
    private fun handlePickupListResponse(response: Response<PickResponse>) {
        if (response.isSuccessful) {
            val pickupResponse = response.body()
            if (pickupResponse != null && pickupResponse.status) {
                try {
                    // isNextPickupTarget이 true인 항목이 먼저 오도록 정렬
                    val sortedList = pickupResponse.data.sortedByDescending { it.isNextPickupTarget }
                    _pickupList.value = sortedList
                    
                    // 로그에 데이터 출력 (디버깅용)
                    android.util.Log.d("API_DEBUG", "수거 목록 ${sortedList.size}개 수신됨")
                    for (item in sortedList.take(3)) { // 처음 3개 항목만 로그로 출력
                        android.util.Log.d("API_DEBUG", "- 항목: 소유자ID=${item.ownerId}, 상태=${item.status}, 주소=${item.address}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("API_ERROR", "수거 목록 처리 중 오류: ${e.message}", e)
                    _errorMessage.value = "데이터 처리 중 오류가 발생했습니다: ${e.message}"
                }
            } else {
                android.util.Log.w("API_DEBUG", "서버 응답 status가 false: ${pickupResponse?.data?.size ?: 0}개 데이터")
                _errorMessage.value = "데이터를 불러올 수 없습니다."
            }
        } else {
            when (response.code()) {
                401 -> {
                    android.util.Log.e("API_ERROR", "인증 오류(401): 토큰이 유효하지 않음")
                    _errorMessage.value = "인증 오류가 발생했습니다. 다시 로그인해주세요."
                }
                else -> {
                    android.util.Log.e("API_ERROR", "서버 오류(${response.code()})")
                    _errorMessage.value = "서버 오류가 발생했습니다 (${response.code()})."
                }
            }
        }
    }
    
    /**
     * 수거 완료 처리를 수행하는 함수
     * 
     * @param token 사용자 인증 토큰
     * @param ownerId 수거 완료 처리할 소유자 ID
     */
    fun completePickup(token: String, ownerId: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        _operationCompleted.value = false
        
        viewModelScope.launch {
            try {
                val response = driverApiService.patchPickComplete(
                    "Bearer $token", 
                    IdRequest(ownerId)
                )
                
                if (response.isSuccessful && response.body()?.status == true) {
                    _operationCompleted.value = true
                    // 수거 완료 후 목록 갱신
                    fetchPickupList(token)
                } else {
                    val errorMsg = response.body()?.message ?: "수거 완료 처리에 실패했습니다."
                    _errorMessage.value = errorMsg
                    when (response.code()) {
                        401 -> _errorMessage.value = "인증 오류가 발생했습니다. 다시 로그인해주세요."
                        404 -> _errorMessage.value = "해당 가게 정보를 찾을 수 없거나 권한이 없습니다."
                        else -> _errorMessage.value = "서버 오류가 발생했습니다 (${response.code()})."
                    }
                }
            } catch (e: IOException) {
                _errorMessage.value = "네트워크 연결 오류가 발생했습니다."
            } catch (e: Exception) {
                _errorMessage.value = "오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 에러 메시지를 초기화하는 함수
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}