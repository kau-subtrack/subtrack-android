package com.please.ui.driver.delivery

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.please.data.api.DriverApiService
import com.please.data.models.driver.*
import com.please.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DriverDeliveryMapViewModel @Inject constructor(
    private val driverApiService: DriverApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val preferenceManager = PreferenceManager(context)

    private val _nextDelivery = MutableLiveData<NextDestinationResponse>()
    val nextDelivery: LiveData<NextDestinationResponse> = _nextDelivery

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _deliveryCompleted = MutableLiveData<Boolean>()
    val deliveryCompleted: LiveData<Boolean> = _deliveryCompleted

    private val _hubArrivalCompleted = MutableLiveData<Boolean>()
    val hubArrivalCompleted: LiveData<Boolean> = _hubArrivalCompleted

    // 🔧 논리적 현재 위치 관리
    private val _logicalCurrentPosition = MutableLiveData<LatLng?>()
    val logicalCurrentPosition: LiveData<LatLng?> = _logicalCurrentPosition

    init {
        // 🔧 앱 시작시 허브를 기본 현재 위치로 설정
        val hubLocation = LatLng(37.5299, 126.9648) // 용산역 허브
        updateLogicalCurrentPosition(hubLocation)
        Log.d("DELIVERY_INIT", "🏢 ViewModel 초기화: 허브를 시작 위치로 설정")
    }

    /**
     * 🔧 논리적 현재 위치 업데이트 (배달 완료 지점, 허브 등)
     */
    fun updateLogicalCurrentPosition(position: LatLng) {
        _logicalCurrentPosition.value = position
        Log.d("DELIVERY_POSITION", "🎯 논리적 현재 위치 업데이트: $position")
    }

    /**
     * 🔧 현재 위치 가져오기 (TSP 계산용)
     */
    fun getCurrentLogicalPosition(): LatLng? {
        return _logicalCurrentPosition.value
    }

    /**
     * 다음 배달지 정보를 가져옵니다 (TSP 최적화 적용)
     */
    fun getNextDelivery() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val token = preferenceManager.getToken()

                if (token.isNullOrEmpty()) {
                    _errorMessage.value = "로그인이 필요합니다"
                    return@launch
                }

                Log.d("DELIVERY_API", "🚀 다음 배달지 요청 시작")

                val response = driverApiService.getNextDelivery("Bearer $token")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _nextDelivery.value = responseBody
                        Log.d("DELIVERY_API", "✅ 다음 배달지 응답: ${responseBody.status}")

                        when (responseBody.status) {
                            "waiting" -> Log.d("DELIVERY_API", "⏰ 대기 상태: ${responseBody.message}")
                            "success" -> {
                                responseBody.nextDestination?.let { dest ->
                                    Log.d("DELIVERY_API", "🎯 다음 배달지: ${dest.name} (${dest.lat}, ${dest.lon})")
                                    Log.d("DELIVERY_API", "📦 deliveryId: ${dest.deliveryId}")
                                    Log.d("DELIVERY_API", "📦 parcelId: ${dest.parcelId}")
                                    Log.d("DELIVERY_API", "📦 getValidDeliveryId(): ${dest.getValidDeliveryId()}")
                                }
                            }
                            "return_to_hub" -> {
                                Log.d("DELIVERY_API", "🏢 허브 복귀")
                                val currentPos = getCurrentLogicalPosition()
                                Log.d("DELIVERY_API", "📍 현재 TSP 위치: $currentPos")
                            }
                            "at_hub" -> Log.d("DELIVERY_API", "✅ 허브 도착 완료")
                        }
                    } else {
                        _errorMessage.value = "서버 응답이 비어있습니다"
                        Log.e("DELIVERY_API", "❌ 빈 응답")
                    }
                } else {
                    handleApiError(response)
                }

            } catch (e: Exception) {
                Log.e("DELIVERY_API", "❌ 다음 배달지 요청 실패", e)
                _errorMessage.value = "네트워크 오류: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 🔧 배달 완료 처리 - 유연한 ID 처리
     */
    fun completeDelivery(id: String) {
        if (id.isEmpty()) {
            _errorMessage.value = "배달 ID가 없습니다"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val token = preferenceManager.getToken()

                if (token.isNullOrEmpty()) {
                    _errorMessage.value = "로그인이 필요합니다"
                    return@launch
                }

                Log.d("DELIVERY_API", "📦 배달 완료 처리 시작: $id")

                // 🔧 서버 요구사항에 맞게 deliveryId 필드로 전송
                val request = CompleteDeliveryRequest(deliveryId = id)
                val response = driverApiService.completeDelivery("Bearer $token", request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        _deliveryCompleted.value = true
                        Log.d("DELIVERY_API", "✅ 배달 완료 성공: $id")

                    } else {
                        _errorMessage.value = responseBody?.message ?: "배달 완료 처리에 실패했습니다"
                        Log.e("DELIVERY_API", "❌ 배달 완료 실패: ${responseBody?.message}")
                    }
                } else {
                    handleApiError(response)
                }

            } catch (e: Exception) {
                Log.e("DELIVERY_API", "❌ 배달 완료 처리 실패", e)
                _errorMessage.value = "배달 완료 처리 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 🔧 허브 도착 완료 처리 (배달용)
     */
    fun completeHubArrival() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val token = preferenceManager.getToken()

                if (token.isNullOrEmpty()) {
                    _errorMessage.value = "로그인이 필요합니다"
                    return@launch
                }

                Log.d("DELIVERY_API", "🏢 허브 도착 완료 처리 시작")

                val response = driverApiService.completeDeliveryHubArrival("Bearer $token")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        _hubArrivalCompleted.value = true
                        Log.d("DELIVERY_API", "✅ 허브 도착 완료")

                        // 🔧 허브 도착시 허브 위치를 현재 위치로 설정
                        val hubLocation = LatLng(37.5299, 126.9648)
                        updateLogicalCurrentPosition(hubLocation)

                    } else {
                        _errorMessage.value = responseBody?.message ?: "허브 도착 처리에 실패했습니다"
                        Log.e("DELIVERY_API", "❌ 허브 도착 실패: ${responseBody?.message}")
                    }
                } else {
                    handleApiError(response)
                }

            } catch (e: Exception) {
                Log.e("DELIVERY_API", "❌ 허브 도착 처리 실패", e)
                _errorMessage.value = "허브 도착 처리 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * API 에러 처리
     */
    private fun <T> handleApiError(response: Response<T>) {
        val errorMessage = when (response.code()) {
            401 -> "인증이 만료되었습니다. 다시 로그인해주세요."
            403 -> "접근 권한이 없습니다."
            404 -> "요청한 정보를 찾을 수 없습니다."
            500 -> "서버 오류가 발생했습니다."
            else -> "알 수 없는 오류가 발생했습니다 (${response.code()})"
        }

        _errorMessage.value = errorMessage
        Log.e("DELIVERY_API", "❌ API 오류: ${response.code()} - $errorMessage")
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearDeliveryCompleted() {
        _deliveryCompleted.value = false
    }

    fun clearHubArrivalCompleted() {
        _hubArrivalCompleted.value = false
    }

    /**
     * 🔧 디버깅용 - 현재 상태 로그
     */
    fun logCurrentState() {
        val currentPos = getCurrentLogicalPosition()
        val nextDest = _nextDelivery.value

        Log.d("DELIVERY_DEBUG", "=== 현재 배달 상태 ===")
        Log.d("DELIVERY_DEBUG", "논리적 현재 위치: $currentPos")
        Log.d("DELIVERY_DEBUG", "다음 배달지 상태: ${nextDest?.status}")
        Log.d("DELIVERY_DEBUG", "로딩 중: ${_isLoading.value}")

        nextDest?.nextDestination?.let { dest ->
            Log.d("DELIVERY_DEBUG", "목적지 deliveryId: ${dest.deliveryId}")
            Log.d("DELIVERY_DEBUG", "목적지 parcelId: ${dest.parcelId}")
            Log.d("DELIVERY_DEBUG", "목적지 getValidDeliveryId(): ${dest.getValidDeliveryId()}")
        }

        Log.d("DELIVERY_DEBUG", "=================")
    }
}