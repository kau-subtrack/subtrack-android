package com.please.ui.driver.deliver

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.api.DriverApiService
import com.please.data.models.driver.DeliveryComResponse
import com.please.data.models.driver.DeliveryList
import com.please.data.models.driver.ParcelStatus
import com.please.data.models.driver.TrackCodeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DriverDeliverViewModel @Inject constructor(
    private val driverApiService: DriverApiService
) : ViewModel() {
    
    // 배송 목록 LiveData
    private val _deliveryList = MutableLiveData<List<DeliveryList>>()
    val deliveryList: LiveData<List<DeliveryList>> = _deliveryList
    
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
     * 배송 목록을 가져오는 함수
     * 
     * @param token 사용자 인증 토큰
     */
    fun fetchDeliveryList(token: String) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val response = driverApiService.getDeliveryList("Bearer $token")
                
                if (response.isSuccessful) {
                    val deliveryResponse = response.body()
                    if (deliveryResponse != null && deliveryResponse.status) {
                        // DELIVERY_COMPLETED 상태를 제외하고 필터링
                        val filteredList = deliveryResponse.data.filter { it.status != ParcelStatus.DELIVERY_COMPLETED }
                        
                        // isNextDeliveryTarget이 true인 항목이 먼저 오도록 정렬
                        val sortedList = filteredList.sortedByDescending { it.isNextDeliveryTarget }
                        _deliveryList.value = sortedList
                    } else {
                        _errorMessage.value = "데이터를 불러올 수 없습니다."
                    }
                } else {
                    when (response.code()) {
                        401 -> _errorMessage.value = "인증 오류가 발생했습니다. 다시 로그인해주세요."
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
     * 배송 완료 처리를 수행하는 함수
     * 
     * @param token 사용자 인증 토큰
     * @param trackingCode 배송 완료 처리할 송장 번호
     */
    fun completeDelivery(token: String, trackingCode: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _operationCompleted.value = false
        
        viewModelScope.launch {
            try {
                val response = driverApiService.patchDeliveryComplete(
                    "Bearer $token", 
                    TrackCodeRequest(trackingCode)
                )
                
                if (response.isSuccessful && response.body()?.status == true) {
                    _operationCompleted.value = true
                    // 배송 완료 후 목록 갱신
                    fetchDeliveryList(token)
                } else {
                    when (response.code()) {
                        401 -> _errorMessage.value = "인증 오류가 발생했습니다. 다시 로그인해주세요."
                        404 -> _errorMessage.value = "해당 송장번호를 찾을 수 없거나 권한이 없습니다."
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
