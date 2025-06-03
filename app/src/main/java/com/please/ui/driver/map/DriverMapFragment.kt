package com.please.ui.driver.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.*
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.please.databinding.FragmentDriverMapBinding
import com.please.data.models.*
import com.please.data.models.driver.Coordinate
import com.please.data.models.driver.DestinationState
import com.please.data.models.driver.NextDestination
import com.please.data.models.driver.RouteResponse
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DriverMapFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentDriverMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DriverMapViewModel by viewModels()
    private lateinit var mapView: MapView
    private var kakaoMap: KakaoMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textToSpeech: TextToSpeech

    // 현재 상태
    private var currentLocation: LatLng? = null
    private var isNavigating = false

    // 권한 요청
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (granted) {
            startLocationTracking()
            // 🔥 권한 허용 후 API 호출 (ViewModel이 상태 관리)
            viewModel.getNextDestination()
        } else {
            Toast.makeText(requireContext(), "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(requireContext().applicationContext, "f353ba92e8fb10d5280a4fbafa486158")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        textToSpeech = TextToSpeech(requireContext(), this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDriverMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView

        setupMap()
        setupUI()
        observeViewModel()
        checkPermissions()
    }

    private fun setupMap() {
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {}
            override fun onMapError(error: Exception) {
                Log.e("MAP", "지도 오류: ${error.message}")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                // 기본 서울 중심으로 설정
                moveCamera(LatLng.from(37.566826, 126.978656), 12)
            }
        })
    }

    private fun setupUI() {
        binding.actionButton.setOnClickListener {
            handleActionButtonClick()
        }
        showLoadingState()
    }

    private fun observeViewModel() {
        // 목적지 상태 관찰
        viewModel.destinationState.observe(viewLifecycleOwner) { state ->
            Log.d("FRAGMENT", "🎯 상태 변경: ${state::class.simpleName}")
            when (state) {
                is DestinationState.Waiting -> {
                    showWaitingState(state.message)
                }
                is DestinationState.WaitingForOrders -> {
                    showWaitingForOrdersState(state.message)
                }
                is DestinationState.NavigateToPickup -> {
                    showNavigationState(state.destination, state.route)
                }
                is DestinationState.ReturnToHub -> {
                    showReturnToHubState(state.route)
                }
                is DestinationState.AtHub -> {
                    showCompletedState()
                }
            }
        }

        // 로딩 상태
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 🔥 수거 완료 처리 - 여기서 다음 TSP 계산 요청
        viewModel.pickupCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                Toast.makeText(requireContext(), "수거 완료! 다음 경로 계산 중...", Toast.LENGTH_SHORT).show()
                Log.d("FRAGMENT", "📦 수거 완료 - 다음 TSP 계산 요청")
                // 🔥 수거 완료 후에만 새로운 TSP 계산
                viewModel.getNextDestination()
            }
        }

        // 허브 도착 완료
        viewModel.hubArrivalCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                Toast.makeText(requireContext(), "오늘 업무 완료! 수고하셨습니다!", Toast.LENGTH_LONG).show()
                showCompletedState()
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking()

            // 🔥 ViewModel이 상태 관리하므로 항상 호출 (중복 방지는 ViewModel에서 처리)
            viewModel.getNextDestination()
        } else {
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationTracking() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateCurrentLocation(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun updateCurrentLocation(location: Location) {
        val newLocation = LatLng.from(location.latitude, location.longitude)
        currentLocation = newLocation

        // 네비게이션 중일 때만 카메라가 따라감
        if (isNavigating) {
            moveCamera(newLocation, 16)
        }

        viewModel.updateCurrentLocation(location.latitude, location.longitude)
    }

    private fun moveCamera(location: LatLng, zoomLevel: Int) {
        val cameraPosition = CameraPosition.from(location.latitude, location.longitude, zoomLevel, 0.0, 0.0, 0.0)
        kakaoMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    // 상태별 UI 업데이트 함수들 (기존과 동일)
    private fun showLoadingState() {
        binding.tvTitle.text = "경로 계산 중..."
        binding.bottomPanel.visibility = View.GONE
        binding.actionButton.visibility = View.GONE
        isNavigating = false
    }

    private fun showWaitingState(message: String) {
        Log.d("FRAGMENT", "⏰ 대기 상태 UI 표시")
        binding.tvTitle.text = "업무 대기 중"
        binding.tvCurrentStatus.text = message
        binding.bottomPanel.visibility = View.VISIBLE
        binding.actionButton.visibility = View.GONE
        binding.tvDestinationAddress.visibility = View.GONE
        binding.tvRemainingCount.text = "-"
        binding.tvEstimatedTime.text = "-"
        binding.tvArrivalTime.text = "-"
        isNavigating = false
    }

    private fun showWaitingForOrdersState(message: String) {
        Log.d("FRAGMENT", "📋 주문 대기 상태 UI 표시")
        binding.tvTitle.text = "새 주문 대기 중"
        binding.tvCurrentStatus.text = message
        binding.bottomPanel.visibility = View.VISIBLE
        binding.actionButton.visibility = View.GONE
        binding.tvDestinationAddress.visibility = View.GONE
        binding.tvRemainingCount.text = "-"
        binding.tvEstimatedTime.text = "-"
        binding.tvArrivalTime.text = "-"
        isNavigating = false
    }

    private fun showNavigationState(destination: NextDestination, route: RouteResponse) {
        Log.d("FRAGMENT", "🎯 수거 네비게이션 UI 표시: ${destination.name}")
        binding.tvTitle.text = "수거지로 이동"
        binding.tvCurrentStatus.text = "다음 목적지로 이동 중..."
        binding.tvDestinationAddress.text = destination.address
        binding.tvDestinationAddress.visibility = View.VISIBLE

        // 남은 수거 개수
        binding.tvRemainingCount.text = "${viewModel.getRemainingPickups()}개"

        // 예상 시간 계산
        route.trip?.summary?.time?.let { timeInSeconds ->
            val minutes = (timeInSeconds / 60).toInt()
            binding.tvEstimatedTime.text = "${minutes}분"

            // 도착 예정 시간 계산
            val currentTime = Calendar.getInstance()
            currentTime.add(Calendar.MINUTE, minutes)
            val arrivalTime = String.format("%02d:%02d",
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE)
            )
            binding.tvArrivalTime.text = arrivalTime
        } ?: run {
            binding.tvEstimatedTime.text = "계산 중"
            binding.tvArrivalTime.text = "-"
        }

        // 목적지로 카메라 이동
        moveCamera(LatLng.from(destination.lat, destination.lon), 15)

        // 경로 그리기 시도
        drawRoute(route.coordinates ?: emptyList())

        // 음성 안내
        route.waypoints?.firstOrNull()?.let { waypoint ->
            announceNavigation(waypoint.instruction)
        }

        // 버튼 설정 - 수거 완료
        binding.actionButton.text = "수거 완료"
        binding.actionButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_green_dark)
        binding.actionButton.visibility = View.VISIBLE
        binding.bottomPanel.visibility = View.VISIBLE

        isNavigating = true
    }

    private fun showReturnToHubState(route: RouteResponse) {
        Log.d("FRAGMENT", "🏠 허브 복귀 UI 표시")
        binding.tvTitle.text = "허브 복귀 중"
        binding.tvCurrentStatus.text = "허브로 복귀 중입니다..."
        binding.tvDestinationAddress.text = "용산역 (허브)"
        binding.tvDestinationAddress.visibility = View.VISIBLE
        binding.tvRemainingCount.text = "0개"

        // 허브까지 시간 계산
        route.trip?.summary?.time?.let { timeInSeconds ->
            val minutes = (timeInSeconds / 60).toInt()
            binding.tvEstimatedTime.text = "${minutes}분"

            val currentTime = Calendar.getInstance()
            currentTime.add(Calendar.MINUTE, minutes)
            val arrivalTime = String.format("%02d:%02d",
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE)
            )
            binding.tvArrivalTime.text = arrivalTime
        } ?: run {
            binding.tvEstimatedTime.text = "계산 중"
            binding.tvArrivalTime.text = "-"
        }

        // 허브로 카메라 이동 (용산역)
        moveCamera(LatLng.from(37.5299, 126.9648), 15)

        // 경로 그리기
        drawRoute(route.coordinates ?: emptyList())

        // 버튼 설정 - 허브 도착
        binding.actionButton.text = "허브 도착"
        binding.actionButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_blue_dark)
        binding.actionButton.visibility = View.VISIBLE
        binding.bottomPanel.visibility = View.VISIBLE

        isNavigating = true
    }

    private fun showCompletedState() {
        Log.d("FRAGMENT", "✅ 업무 완료 UI 표시")
        binding.tvTitle.text = "업무 완료"
        binding.tvCurrentStatus.text = "오늘 업무가 모두 완료되었습니다!"
        binding.tvDestinationAddress.visibility = View.GONE
        binding.tvRemainingCount.text = "0개"
        binding.tvEstimatedTime.text = "-"
        binding.tvArrivalTime.text = "-"
        binding.bottomPanel.visibility = View.VISIBLE
        binding.actionButton.visibility = View.GONE
        isNavigating = false
    }

    private fun handleActionButtonClick() {
        Log.d("FRAGMENT", "🔘 액션 버튼 클릭: ${binding.actionButton.text}")
        when (binding.actionButton.text.toString()) {
            "수거 완료" -> {
                Log.d("FRAGMENT", "📦 수거 완료 버튼 클릭")
                viewModel.completeCurrentPickup()
            }
            "허브 도착" -> {
                Log.d("FRAGMENT", "🏠 허브 도착 버튼 클릭")
                viewModel.completeHubArrival()
            }
        }
    }

    private fun drawRoute(coordinates: List<Coordinate>) {
        if (coordinates.isEmpty()) return

        try {
            Log.d("MAP", "🗺️ 경로 그리기: ${coordinates.size}개 지점")
            // TODO: 실제 폴리라인 그리기 구현
        } catch (e: Exception) {
            Log.e("MAP", "❌ 경로 그리기 오류: ${e.message}")
        }
    }

    private fun announceNavigation(instruction: String) {
        if (::textToSpeech.isInitialized && !textToSpeech.isSpeaking) {
            textToSpeech.speak(instruction, TextToSpeech.QUEUE_FLUSH, null, null)
        }
        Log.d("TTS", "🔊 음성 안내: $instruction")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.KOREAN)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
        Log.d("FRAGMENT", "📱 Fragment onResume - ViewModel이 상태 관리")
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
        Log.d("FRAGMENT", "📱 Fragment onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::textToSpeech.isInitialized) {
            textToSpeech.shutdown()
        }
        Log.d("FRAGMENT", "📱 Fragment onDestroyView")
        _binding = null
    }
}