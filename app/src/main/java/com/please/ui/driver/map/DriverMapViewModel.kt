package com.please.ui.driver.map

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
import kotlinx.coroutines.delay
import retrofit2.Response
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DriverMapViewModel @Inject constructor(
    private val driverApiService: DriverApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val preferenceManager = PreferenceManager(context)

    private val _nextDestination = MutableLiveData<NextDestinationResponse>()
    val nextDestination: LiveData<NextDestinationResponse> = _nextDestination

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _pickupCompleted = MutableLiveData<Boolean>()
    val pickupCompleted: LiveData<Boolean> = _pickupCompleted

    private val _hubArrivalCompleted = MutableLiveData<Boolean>()
    val hubArrivalCompleted: LiveData<Boolean> = _hubArrivalCompleted

    // 🔧 논리적 현재 위치 관리 (GPS 제거됨)
    private val _logicalCurrentPosition = MutableLiveData<LatLng?>()
    val logicalCurrentPosition: LiveData<LatLng?> = _logicalCurrentPosition

    init {
        // 🔧 앱 시작시 허브를 기본 현재 위치로 설정
        val hubLocation = LatLng(37.5299, 126.9648) // 용산역 허브
        updateLogicalCurrentPosition(hubLocation)
        Log.d("TSP_INIT", "🏢 ViewModel 초기화: 허브를 시작 위치로 설정")

        // 타이머 시작
        startPeriodicAllCompletedCheck()
    }

    /**
     * 오후 3시 이후 10분마다 전체 완료 체크
     */
    private fun startPeriodicAllCompletedCheck() {
        viewModelScope.launch {
            while (true) {
                delay(600000) // 10분 대기

                val currentTime = Calendar.getInstance()
                val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

                if (currentHour >= 15) { // 오후 3시 이후
                    checkAllCompleted()
                }
            }
        }
    }

    /**
     * 전체 완료 상태 체크
     */
    private suspend fun checkAllCompleted() {
        try {
            val response = driverApiService.checkAllCompleted()

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody?.completed == true) {
                    Log.d("ALL_COMPLETED", "✅ 모든 수거 완료, 배달로 전환됨")
                    // 필요시 UI 업데이트나 토스트 메시지 추가 가능
                } else {
                    Log.d("ALL_COMPLETED", "🔄 아직 미완료 수거가 있음")
                }
            }
        } catch (e: Exception) {
            Log.e("ALL_COMPLETED", "❌ 전체 완료 체크 실패", e)
        }
    }

    /**
     * 🔧 논리적 현재 위치 업데이트 (수거 완료 지점, 허브 등)
     */
    fun updateLogicalCurrentPosition(position: LatLng) {
        _logicalCurrentPosition.value = position
        Log.d("TSP_POSITION", "🎯 논리적 현재 위치 업데이트: $position")
    }

    /**
     * 🔧 현재 위치 가져오기 (TSP 계산용)
     */
    fun getCurrentLogicalPosition(): LatLng? {
        return _logicalCurrentPosition.value
    }

    /**
     * 다음 목적지 정보를 가져옵니다 (TSP 최적화 적용)
     */
    fun getNextDestination() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val token = preferenceManager.getToken()

                if (token.isNullOrEmpty()) {
                    _errorMessage.value = "로그인이 필요합니다"
                    return@launch
                }

                Log.d("TSP_API", "🚀 다음 목적지 요청 시작")

                val response = driverApiService.getNextDestination("Bearer $token")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _nextDestination.value = responseBody!!
                        Log.d("TSP_API", "✅ 다음 목적지 응답: ${responseBody.status}")

                        when (responseBody.status) {
                            "waiting" -> Log.d("TSP_API", "⏰ 대기 상태: ${responseBody.message}")
                            "waiting_for_orders" -> Log.d("TSP_API", "📋 신규 요청 대기: ${responseBody.message}")
                            "success" -> {
                                responseBody.nextDestination?.let { dest ->
                                    Log.d("TSP_API", "🎯 다음 목적지: ${dest.name} (${dest.lat}, ${dest.lon})")
                                }
                            }
                            "return_to_hub" -> {
                                Log.d("TSP_API", "🏢 허브 복귀")
                                val currentPos = getCurrentLogicalPosition()
                                Log.d("TSP_API", "📍 현재 TSP 위치: $currentPos")
                            }
                            "at_hub" -> Log.d("TSP_API", "✅ 허브 도착 완료")
                        }
                    } else {
                        _errorMessage.value = "서버 응답이 비어있습니다"
                        Log.e("TSP_API", "❌ 빈 응답")
                    }
                } else {
                    handleApiError(response)
                }

            } catch (e: Exception) {
                Log.e("TSP_API", "❌ 다음 목적지 요청 실패", e)
                _errorMessage.value = "네트워크 오류: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 🔧 수거 완료 처리 (TSP) - 완료 후 위치 업데이트 포함
     */
    fun completePickup(parcelId: String) {
        if (parcelId.isEmpty()) {
            _errorMessage.value = "소포 ID가 없습니다"
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

                Log.d("TSP_API", "📦 수거 완료 처리 시작: $parcelId")

                val request = CompletePickupRequest(parcelId = parcelId)
                val response = driverApiService.completePickup("Bearer $token", request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        _pickupCompleted.value = true
                        Log.d("TSP_API", "✅ 수거 완료 성공: $parcelId")

                    } else {
                        _errorMessage.value = responseBody?.message ?: "수거 완료 처리에 실패했습니다"
                        Log.e("TSP_API", "❌ 수거 완료 실패: ${responseBody?.message}")
                    }
                } else {
                    handleApiError(response)
                }

            } catch (e: Exception) {
                Log.e("TSP_API", "❌ 수거 완료 처리 실패", e)
                _errorMessage.value = "수거 완료 처리 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 🔧 허브 도착 완료 처리 (TSP)
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

                Log.d("TSP_API", "🏢 허브 도착 완료 처리 시작")

                val response = driverApiService.completeHubArrival("Bearer $token")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        _hubArrivalCompleted.value = true
                        Log.d("TSP_API", "✅ 허브 도착 완료")

                        // 🔧 허브 도착시 허브 위치를 현재 위치로 설정
                        val hubLocation = LatLng(37.5299, 126.9648)
                        updateLogicalCurrentPosition(hubLocation)

                    } else {
                        _errorMessage.value = responseBody?.message ?: "허브 도착 처리에 실패했습니다"
                        Log.e("TSP_API", "❌ 허브 도착 실패: ${responseBody?.message}")
                    }
                } else {
                    handleApiError(response)
                }

            } catch (e: Exception) {
                Log.e("TSP_API", "❌ 허브 도착 처리 실패", e)
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
        Log.e("TSP_API", "❌ API 오류: ${response.code()} - $errorMessage")
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearPickupCompleted() {
        _pickupCompleted.value = false
    }

    fun clearHubArrivalCompleted() {
        _hubArrivalCompleted.value = false
    }

    /**
     * 🔧 디버깅용 - 현재 상태 로그
     */
    fun logCurrentState() {
        val currentPos = getCurrentLogicalPosition()
        val nextDest = _nextDestination.value

        Log.d("TSP_DEBUG", "=== 현재 TSP 상태 ===")
        Log.d("TSP_DEBUG", "논리적 현재 위치: $currentPos")
        Log.d("TSP_DEBUG", "다음 목적지 상태: ${nextDest?.status}")
        Log.d("TSP_DEBUG", "로딩 중: ${_isLoading.value}")
        Log.d("TSP_DEBUG", "=================")
    }
}