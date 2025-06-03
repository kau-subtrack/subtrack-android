package com.please.ui.driver.map

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.*
import com.please.data.api.TspApiService
import com.please.data.models.driver.CompletePickupRequest
import com.please.data.models.driver.DestinationState
import com.please.data.models.driver.NextDestinationResponse
import com.please.data.models.driver.Waypoint
import com.please.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverMapViewModel @Inject constructor(
    private val tspApiService: TspApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // PreferenceManager 초기화
    private val preferenceManager: PreferenceManager by lazy {
        PreferenceManager(context)
    }

    // UI States
    private val _destinationState = MutableLiveData<DestinationState>()
    val destinationState: LiveData<DestinationState> = _destinationState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _pickupCompleted = MutableLiveData<Boolean>()
    val pickupCompleted: LiveData<Boolean> = _pickupCompleted

    private val _hubArrivalCompleted = MutableLiveData<Boolean>()
    val hubArrivalCompleted: LiveData<Boolean> = _hubArrivalCompleted

    // Current Data
    private var currentParcelId: String? = null
    private var remainingPickups: Int = 0
    private var currentWaypoints: List<Waypoint> = emptyList()

    // 실제 현재 위치 (GPS에서 업데이트됨)
    private var currentLat: Double = 37.566826 // 기본값 (서울시청)
    private var currentLon: Double = 126.978656

    // 🔥 핵심 수정: 상태 관리 강화
    private var isCurrentlyOnPickup = false  // 현재 수거 중인지 (수거 완료 전까지 true)
    private var isCurrentlyReturningToHub = false  // 허브 복귀 중인지
    private var savedPickupState: DestinationState.NavigateToPickup? = null  // 저장된 수거 상태
    private var savedHubReturnState: DestinationState.ReturnToHub? = null  // 저장된 허브 복귀 상태

    // ViewModel이 생성된 이후 첫 API 호출인지 확인
    private var hasEverCalledApi = false

    /**
     * 🔥 메인 함수: 다음 목적지 가져오기
     * Fragment에서 호출되지만, 수거 중이면 저장된 상태 사용
     */
    fun getNextDestination() {
        // 🔥 수거 중이면 기존 상태 유지 (TSP 재계산 안함!)
        if (isCurrentlyOnPickup && savedPickupState != null) {
            Log.d("STATE", "🚫 수거 중이므로 TSP 재계산 차단 - 기존 수거 상태 유지")
            _destinationState.value = savedPickupState
            return
        }

        // 🔥 허브 복귀 중이면 기존 상태 유지
        if (isCurrentlyReturningToHub && savedHubReturnState != null) {
            Log.d("STATE", "🚫 허브 복귀 중이므로 상태 유지 - TSP 재계산 안함")
            _destinationState.value = savedHubReturnState
            return
        }

        // 🔥 첫 호출이거나, 수거/허브복귀가 완료된 후에만 새로운 TSP 계산
        Log.d("STATE", "✅ 새로운 TSP 계산 시작 (수거 완료 후 또는 첫 호출)")
        callNextDestinationApi()
    }

    /**
     * 실제 API 호출 함수 (내부에서만 사용)
     */
    private fun callNextDestinationApi() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("API", "🔄 다음 목적지 API 호출 시작 - TSP 최적화 계산")

                val authToken = getAuthToken()
                if (authToken.isNullOrEmpty()) {
                    Log.e("API", "❌ 인증 토큰이 없습니다")
                    setWaitingState("로그인이 필요합니다.")
                    return@launch
                }

                // 실제 API 호출 (TSP 최적화 수행)
                val response = tspApiService.getNextDestination("Bearer $authToken")

                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        Log.d("API", "✅ 다음 목적지 API 성공: ${data.status}")
                        hasEverCalledApi = true
                        handleApiResponse(data)
                    } ?: run {
                        Log.e("API", "❌ 응답 body가 null입니다")
                        setWaitingState("서버 응답 오류가 발생했습니다.")
                    }
                } else {
                    Log.e("API", "❌ API 호출 실패: ${response.code()} - ${response.message()}")
                    handleApiError(response.code())
                }

            } catch (e: Exception) {
                Log.e("API", "❌ API 호출 예외: ${e.message}", e)
                setWaitingState("네트워크 연결을 확인해주세요.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * API 응답 처리
     */
    private fun handleApiResponse(response: NextDestinationResponse) {
        when (response.status) {
            "waiting" -> {
                Log.d("STATE", "⏰ 업무 시작 대기 상태")
                clearAllStates()
                _destinationState.value = DestinationState.Waiting(
                    response.message ?: "업무 시작 시간을 기다리는 중..."
                )
            }

            "waiting_for_orders" -> {
                Log.d("STATE", "📋 새 주문 대기 상태")
                clearAllStates()
                _destinationState.value = DestinationState.WaitingForOrders(
                    response.message ?: "새로운 수거 요청을 기다리는 중..."
                )
            }

            "success" -> {
                val destination = response.nextDestination
                val route = response.route

                if (destination == null || route == null) {
                    Log.e("API", "❌ 목적지나 경로 정보가 없습니다")
                    setWaitingState("경로 정보를 가져올 수 없습니다.")
                    return
                }

                // 🔥 수거 상태 시작
                currentParcelId = destination.parcelId
                remainingPickups = response.remainingPickups
                currentWaypoints = route.waypoints ?: emptyList()

                Log.d("STATE", "🎯 새 수거 시작: ${destination.name}")
                Log.d("STATE", "📦 남은 수거: ${remainingPickups}개")
                Log.d("STATE", "🔒 수거 상태 잠금 - 완료 버튼 누를 때까지 경로 고정")

                val pickupState = DestinationState.NavigateToPickup(
                    destination = destination,
                    route = route
                )

                // 🔥 수거 상태 저장 및 잠금
                isCurrentlyOnPickup = true
                isCurrentlyReturningToHub = false
                savedPickupState = pickupState
                savedHubReturnState = null

                _destinationState.value = pickupState
            }

            "return_to_hub" -> {
                val route = response.route
                if (route == null) {
                    Log.e("API", "❌ 허브 복귀 경로 정보가 없습니다")
                    setWaitingState("허브 복귀 경로를 가져올 수 없습니다.")
                    return
                }

                Log.d("STATE", "🏠 허브 복귀 시작")
                Log.d("STATE", "🔒 허브 복귀 상태 잠금 - 도착 버튼 누를 때까지 고정")

                val hubReturnState = DestinationState.ReturnToHub(route)

                // 🔥 허브 복귀 상태 저장 및 잠금
                isCurrentlyOnPickup = false
                isCurrentlyReturningToHub = true
                savedPickupState = null
                savedHubReturnState = hubReturnState

                _destinationState.value = hubReturnState
            }

            "at_hub" -> {
                Log.d("STATE", "✅ 허브 도착 완료 - 오늘 업무 종료")
                clearAllStates()
                _destinationState.value = DestinationState.AtHub
            }

            else -> {
                Log.e("API", "❌ 알 수 없는 상태: ${response.status}")
                clearAllStates()
                setWaitingState("알 수 없는 응답을 받았습니다.")
            }
        }
    }

    /**
     * 🔥 수거 완료 처리 - 여기서만 수거 상태 해제!
     */
    fun completeCurrentPickup() {
        if (currentParcelId.isNullOrEmpty()) {
            Log.e("API", "❌ 완료할 소포 ID가 없습니다")
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("API", "📦 수거 완료 API 호출: $currentParcelId")

                val authToken = getAuthToken()
                if (authToken.isNullOrEmpty()) {
                    Log.e("API", "❌ 인증 토큰이 없습니다")
                    return@launch
                }

                // 실제 API 호출
                val response = tspApiService.completePickup(
                    "Bearer $authToken",
                    CompletePickupRequest(currentParcelId!!)
                )

                if (response.isSuccessful) {
                    Log.d("API", "✅ 수거 완료 성공")
                    handlePickupCompletion()
                } else {
                    Log.e("API", "❌ 수거 완료 실패: ${response.code()}")
                    // UX를 위해 실패해도 완료 처리
                    handlePickupCompletion()
                }

            } catch (e: Exception) {
                Log.e("API", "❌ 수거 완료 API 예외: ${e.message}", e)
                // UX를 위해 예외 발생해도 완료 처리
                handlePickupCompletion()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 수거 완료 후 상태 클리어
     */
    private fun handlePickupCompletion() {
        Log.d("STATE", "🔓 수거 완료 - 상태 잠금 해제")

        // 🔥 수거 상태 완전 클리어
        isCurrentlyOnPickup = false
        savedPickupState = null
        currentParcelId = null

        _pickupCompleted.value = true

        Log.d("STATE", "✅ 다음 TSP 계산 준비 완료")
    }

    /**
     * 🔥 허브 도착 완료 처리
     */
    fun completeHubArrival() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("API", "🏠 허브 도착 API 호출")

                val authToken = getAuthToken()
                if (authToken.isNullOrEmpty()) {
                    Log.e("API", "❌ 인증 토큰이 없습니다")
                    return@launch
                }

                val response = tspApiService.hubArrived("Bearer $authToken")

                if (response.isSuccessful) {
                    Log.d("API", "✅ 허브 도착 완료 성공")
                    handleHubArrivalCompletion()
                } else {
                    Log.e("API", "❌ 허브 도착 실패: ${response.code()}")
                    handleHubArrivalCompletion()
                }

            } catch (e: Exception) {
                Log.e("API", "❌ 허브 도착 API 예외: ${e.message}", e)
                handleHubArrivalCompletion()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 허브 도착 완료 후 상태 클리어
     */
    private fun handleHubArrivalCompletion() {
        Log.d("STATE", "🔓 허브 도착 완료 - 모든 상태 클리어")
        clearAllStates()
        _hubArrivalCompleted.value = true
    }

    /**
     * 모든 상태 클리어
     */
    private fun clearAllStates() {
        isCurrentlyOnPickup = false
        isCurrentlyReturningToHub = false
        savedPickupState = null
        savedHubReturnState = null
        currentParcelId = null
    }

    /**
     * 대기 상태 설정 헬퍼
     */
    private fun setWaitingState(message: String) {
        clearAllStates()
        _destinationState.value = DestinationState.WaitingForOrders(message)
    }

    /**
     * API 에러 처리
     */
    private fun handleApiError(errorCode: Int) {
        val message = when (errorCode) {
            401 -> "인증이 만료되었습니다. 다시 로그인해주세요."
            404 -> "서비스를 찾을 수 없습니다."
            500 -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            else -> "네트워크 오류가 발생했습니다. ($errorCode)"
        }
        setWaitingState(message)
    }

    // 기존 함수들 유지
    fun updateCurrentLocation(lat: Double, lon: Double) {
        currentLat = lat
        currentLon = lon
        Log.d("LOCATION", "📍 현재 위치 업데이트: ($lat, $lon)")
    }

    fun getRemainingPickups(): Int = remainingPickups
    fun getCurrentWaypoints(): List<Waypoint> = currentWaypoints

    private fun getAuthToken(): String? {
        val token = preferenceManager.getToken()
        if (token.isNullOrEmpty()) {
            Log.w("AUTH", "⚠️ 저장된 인증 토큰을 찾을 수 없습니다")
        } else {
            Log.d("AUTH", "🔑 토큰 사용: ${token.take(20)}...")
        }
        return token
    }
}