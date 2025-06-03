package com.please.ui.driver.map

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.please.R
import com.please.databinding.FragmentDriverMapBinding
import com.please.data.models.driver.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverMapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentDriverMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DriverMapViewModel by viewModels()

    private var googleMap: GoogleMap? = null

    // 🔧 마커 관리 (GPS 완전 제거)
    private var currentLocationMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var routePolyline: Polyline? = null

    // 🔧 논리적 현재 위치만 관리
    private var logicalCurrentLocation: LatLng? = null
    private var isNavigationStarted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeMap()
        setupObservers()
        setupClickListeners()

        // 🔧 허브를 기본 시작 위치로 설정
        initializeLogicalPosition()

        // 처음 로드시 다음 목적지 요청
        viewModel.getNextDestination()
    }

    private fun initializeMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    // 🔧 허브를 기본 시작 위치로 설정
    private fun initializeLogicalPosition() {
        val hubLocation = LatLng(37.5299, 126.9648) // 용산역 허브
        updateLogicalCurrentLocation(hubLocation, "허브 (시작 위치)")
        viewModel.updateLogicalCurrentPosition(hubLocation)
        Log.d("TSP_INIT", "🏢 허브를 시작 위치로 설정: $hubLocation")
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map.apply {
            uiSettings.apply {
                isZoomControlsEnabled = false
                isCompassEnabled = true
                isMyLocationButtonEnabled = false // GPS 관련 UI 비활성화
            }

            // 🔧 현재 위치로 이동 (새로고침 버튼 길게 누르기)
            binding.btnRefresh.setOnLongClickListener {
                logicalCurrentLocation?.let { location ->
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                    Toast.makeText(requireContext(), "🎯 현재 위치로 이동", Toast.LENGTH_SHORT).show()
                    true
                } ?: false
            }
        }

        // 서울 중심으로 초기 설정
        val seoul = LatLng(37.5665, 126.9780)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12f))
    }

    // 🔧 논리적 현재 위치 업데이트 (GPS 제거됨)
    private fun updateLogicalCurrentLocation(location: LatLng, title: String = "현재 위치") {
        logicalCurrentLocation = location

        currentLocationMarker?.remove()
        currentLocationMarker = googleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title(title)
                .snippet("TSP 계산 기준점")
                .icon(createCurrentLocationIcon())
                .zIndex(10f)
        )

        Log.d("TSP_LOCATION", "🎯 논리적 현재 위치 업데이트: $location ($title)")
    }

    // 🔧 현재 위치용 커스텀 아이콘 (초록색 + 명확한 표시)
    private fun createCurrentLocationIcon(): BitmapDescriptor {
        val size = 48
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 배경 원
        val backgroundPaint = Paint().apply {
            color = 0xFF4CAF50.toInt() // 초록색
            isAntiAlias = true
        }

        // 테두리
        val borderPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            isAntiAlias = true
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }

        // 중앙 점
        val centerPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            isAntiAlias = true
        }

        val center = size / 2f
        val outerRadius = center - 4
        val innerRadius = 4f

        // 배경 원
        canvas.drawCircle(center, center, outerRadius, backgroundPaint)
        // 테두리
        canvas.drawCircle(center, center, outerRadius, borderPaint)
        // 중앙 점
        canvas.drawCircle(center, center, innerRadius, centerPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // 🔧 목적지 마커 (빨간 핀)
    private fun createDestinationIcon(): BitmapDescriptor {
        val size = 56
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 빨간색 핀 모양 그리기
        val paint = Paint().apply {
            color = 0xFFE53E3E.toInt() // 빨간색
            isAntiAlias = true
        }

        val borderPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            isAntiAlias = true
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

        val center = size / 2f
        val radius = 18f

        // 핀 몸체 (원)
        canvas.drawCircle(center, center - 8, radius, paint)
        canvas.drawCircle(center, center - 8, radius, borderPaint)

        // 핀 끝 (삼각형)
        val path = Path().apply {
            moveTo(center - 12, center + 8)
            lineTo(center, center + 20)
            lineTo(center + 12, center + 8)
            close()
        }
        canvas.drawPath(path, paint)
        canvas.drawPath(path, borderPaint)

        // 중앙 흰 점
        val innerPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            isAntiAlias = true
        }
        canvas.drawCircle(center, center - 8, 6f, innerPaint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun setupObservers() {
        // 🔧 ViewModel의 논리적 현재 위치 관찰
        viewModel.logicalCurrentPosition.observe(viewLifecycleOwner) { position ->
            position?.let {
                updateLogicalCurrentLocation(it, "TSP 기준 위치")
            }
        }

        // 다음 목적지 관찰
        viewModel.nextDestination.observe(viewLifecycleOwner) { response ->
            Log.d("TSP_UI", "🎯 다음 목적지 응답: ${response.status}")

            when (response.status) {
                "waiting" -> {
                    showWaitingState(response.message ?: "대기 중...")
                }
                "waiting_for_orders" -> {
                    showWaitingForOrdersState(response.message ?: "신규 요청 대기 중...")
                }
                "success" -> {
                    response.nextDestination?.let { destination ->
                        Log.d("TSP_UI", "📍 수거 목적지: ${destination.name} (${destination.lat}, ${destination.lon})")
                        showNavigationToPickup(destination, response.route)
                    }
                }
                "return_to_hub" -> {
                    Log.d("TSP_UI", "🏢 허브 복귀")
                    response.route?.let { route ->
                        showReturnToHub(route)
                    }
                }
                "at_hub" -> {
                    Log.d("TSP_UI", "✅ 허브 도착 완료")
                    showAtHubState()
                }
            }
        }

        // 로딩 상태 관찰
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 에러 메시지 관찰
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // 🔧 수거 완료 결과 관찰
        viewModel.pickupCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                Toast.makeText(requireContext(), "✅ 수거가 완료되었습니다!", Toast.LENGTH_SHORT).show()

                // 🔧 수거 완료한 지점을 새로운 논리적 현재 위치로 설정
                val completedLocation = destinationMarker?.position
                completedLocation?.let {
                    Log.d("TSP_UI", "📍 수거 완료! 새 현재 위치로 설정: $it")
                    viewModel.updateLogicalCurrentPosition(it)
                    updateLogicalCurrentLocation(it, "수거 완료 지점")
                }

                viewModel.clearPickupCompleted()

                // 잠시 후 다음 목적지 요청
                binding.root.postDelayed({
                    viewModel.getNextDestination()
                }, 1000)
            }
        }

        // 허브 도착 완료 관찰
        viewModel.hubArrivalCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                Toast.makeText(requireContext(), "🏢 허브 도착 완료! 수고하셨습니다!", Toast.LENGTH_LONG).show()

                // 🔧 허브 도착시 허브를 현재 위치로 설정
                val hubLocation = LatLng(37.5299, 126.9648)
                viewModel.updateLogicalCurrentPosition(hubLocation)
                updateLogicalCurrentLocation(hubLocation, "허브 (용산역)")

                showWorkCompletedState()
                viewModel.clearHubArrivalCompleted()
            }
        }
    }

    private fun setupClickListeners() {
        // 수거 완료 버튼
        binding.btnComplete.setOnClickListener {
            Log.d("BUTTON", "🔘 수거 완료 버튼 클릭")

            val destination = viewModel.nextDestination.value?.nextDestination
            val parcelId = destination?.parcelId

            if (!parcelId.isNullOrEmpty()) {
                Log.d("TSP_UI", "📦 수거 완료 처리: $parcelId")
                viewModel.completePickup(parcelId)
            } else {
                Toast.makeText(requireContext(), "수거할 소포 정보가 없습니다", Toast.LENGTH_SHORT).show()
                Log.w("TSP_UI", "⚠️ parcelId가 없음")
            }
        }

        // 허브 도착 버튼
        binding.btnArriveHub.setOnClickListener {
            Log.d("BUTTON", "🏢 허브 도착 버튼 클릭")
            viewModel.completeHubArrival()
        }

        // 새로고침 버튼
        binding.btnRefresh.setOnClickListener {
            Log.d("BUTTON", "🔄 새로고침 버튼 클릭")
            viewModel.getNextDestination()
        }

        // 🔧 내 위치 버튼 - 현재 논리적 위치로 이동
        binding.btnMyLocation.setOnClickListener {
            logicalCurrentLocation?.let { location ->
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                Toast.makeText(requireContext(), "📍 현재 위치로 이동", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(requireContext(), "현재 위치 정보가 없습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔧 외부 네비게이션 시작 버튼
        binding.btnNavigation?.setOnClickListener {
            val destination = viewModel.nextDestination.value?.nextDestination
            destination?.let { dest ->
                startExternalNavigation(dest.lat, dest.lon, dest.name)
            } ?: run {
                Toast.makeText(requireContext(), "목적지 정보가 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔧 외부 네비게이션 앱 실행
    private fun startExternalNavigation(lat: Double, lon: Double, name: String) {
        try {
            // 구글맵 네비게이션 실행
            val gmmIntentUri = android.net.Uri.parse("google.navigation:q=$lat,$lon")
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(mapIntent)
                Log.d("NAVIGATION", "🗺️ 구글맵 네비게이션 시작: $name")
            } else {
                // 구글맵이 없으면 기본 지도 앱으로
                val fallbackUri = android.net.Uri.parse("geo:$lat,$lon?q=$lat,$lon($name)")
                val fallbackIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, fallbackUri)
                startActivity(fallbackIntent)
                Log.d("NAVIGATION", "🗺️ 기본 지도 앱으로 네비게이션 시작: $name")
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "네비게이션 앱을 실행할 수 없습니다", Toast.LENGTH_SHORT).show()
            Log.e("NAVIGATION", "❌ 네비게이션 실행 실패", e)
        }
    }

    private fun showWaitingState(message: String) {
        binding.apply {
            tvStatus.text = "⏰ 대기 중"
            tvDestination.text = message
            tvDistance.text = ""
            tvEta.text = ""

            btnComplete.visibility = View.GONE
            btnArriveHub.visibility = View.GONE
            btnRefresh.visibility = View.VISIBLE
        }

        clearMapMarkersAndRoute()
        isNavigationStarted = false
    }

    private fun showWaitingForOrdersState(message: String) {
        binding.apply {
            tvStatus.text = "📋 신규 요청 대기"
            tvDestination.text = message
            tvDistance.text = ""
            tvEta.text = ""

            btnComplete.visibility = View.GONE
            btnArriveHub.visibility = View.GONE
            btnRefresh.visibility = View.VISIBLE
        }

        clearMapMarkersAndRoute()
    }

    private fun showNavigationToPickup(destination: NextDestination, route: RouteResponse?) {
        isNavigationStarted = true

        binding.apply {
            tvStatus.text = "🚚 수거 진행 중"
            tvDestination.text = "${destination.name}\n${destination.address}"

            route?.trip?.summary?.let { summary ->
                tvDistance.text = "거리: ${String.format("%.1f", summary.length)} km"
                tvEta.text = "예상시간: ${(summary.time / 60).toInt()}분"
                Log.d("TSP_UI", "📏 거리: ${summary.length}km, 시간: ${summary.time}초")
            }

            btnComplete.visibility = View.VISIBLE
            btnArriveHub.visibility = View.GONE
            btnRefresh.visibility = View.VISIBLE
        }

        val destinationLatLng = LatLng(destination.lat, destination.lon)
        showDestinationOnMap(destinationLatLng, destination.name)
        Log.d("TSP_UI", "📍 목적지 마커 표시: (${destination.lat}, ${destination.lon})")

        route?.let {
            showRouteOnMap(it)
            Log.d("TSP_UI", "🛣️ 경로 표시: ${it.coordinates?.size ?: 0}개 좌표")
        }
    }

    private fun showReturnToHub(route: RouteResponse) {
        binding.apply {
            tvStatus.text = "🏢 허브 복귀 중"
            tvDestination.text = "모든 수거 완료\n허브로 복귀해주세요"

            route.trip?.summary?.let { summary ->
                tvDistance.text = "거리: ${String.format("%.1f", summary.length)} km"
                tvEta.text = "예상시간: ${(summary.time / 60).toInt()}분"
            }

            btnComplete.visibility = View.GONE
            btnArriveHub.visibility = View.VISIBLE
            btnRefresh.visibility = View.VISIBLE
        }

        val hubLocation = LatLng(37.5299, 126.9648)
        showDestinationOnMap(hubLocation, "용산역 허브")
        showRouteOnMap(route)
    }

    private fun showAtHubState() {
        binding.apply {
            tvStatus.text = "✅ 업무 완료"
            tvDestination.text = "허브에 도착했습니다\n오늘 업무가 완료되었습니다!"
            tvDistance.text = ""
            tvEta.text = ""

            btnComplete.visibility = View.GONE
            btnArriveHub.visibility = View.GONE
            btnRefresh.visibility = View.VISIBLE
        }

        clearMapMarkersAndRoute()
    }

    private fun showWorkCompletedState() {
        binding.apply {
            tvStatus.text = "🎉 수고하셨습니다!"
            tvDestination.text = "오늘의 모든 업무가 완료되었습니다"
            tvDistance.text = ""
            tvEta.text = ""

            btnComplete.visibility = View.GONE
            btnArriveHub.visibility = View.GONE
            btnRefresh.visibility = View.VISIBLE
        }
    }

    private fun showDestinationOnMap(destination: LatLng, title: String) {
        destinationMarker?.remove()

        destinationMarker = googleMap?.addMarker(
            MarkerOptions()
                .position(destination)
                .title(title)
                .snippet("목적지")
                .icon(createDestinationIcon())
                .zIndex(15f) // 가장 위에 표시
        )

        // 🔧 스마트한 카메라 조정
        logicalCurrentLocation?.let { current ->
            val distance = calculateDistance(current, destination)

            if (distance < 100) { // 100미터 미만이면 단일 지점으로 표시
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(destination, 18f) // 더 확대
                )
                Log.d("TSP_UI", "📷 카메라: 가까운 거리로 확대")
            } else {
                // 멀리 떨어져 있으면 둘 다 보이게
                val builder = LatLngBounds.Builder()
                builder.include(current)
                builder.include(destination)

                val bounds = builder.build()
                val padding = 200

                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds, padding)
                )
                Log.d("TSP_UI", "📷 카메라: 현재위치 & 목적지 포함")
            }
        } ?: run {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(destination, 15f)
            )
            Log.d("TSP_UI", "📷 카메라: 목적지만")
        }
    }

    // 🔧 거리 계산 함수
    private fun calculateDistance(pos1: LatLng, pos2: LatLng): Double {
        val earthRadius = 6371000.0 // 지구 반지름(미터)

        val lat1Rad = Math.toRadians(pos1.latitude)
        val lat2Rad = Math.toRadians(pos2.latitude)
        val deltaLatRad = Math.toRadians(pos2.latitude - pos1.latitude)
        val deltaLonRad = Math.toRadians(pos2.longitude - pos1.longitude)

        val a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    private fun showRouteOnMap(route: RouteResponse) {
        routePolyline?.remove()

        route.coordinates?.let { coordinates ->
            if (coordinates.isNotEmpty()) {
                val polylineOptions = PolylineOptions().apply {
                    coordinates.forEach { coord ->
                        add(LatLng(coord.lat, coord.lon))
                    }

                    // 🎨 네비게이션 스타일 설정
                    color(0xFF4285F4.toInt())  // 구글 블루 색상
                    width(10f)                 // 적당한 두께
                    geodesic(true)            // 지구 곡률 반영
                    jointType(JointType.ROUND) // 둥근 연결점
                    startCap(RoundCap())      // 둥근 시작점
                    endCap(RoundCap())        // 둥근 끝점
                }

                routePolyline = googleMap?.addPolyline(polylineOptions)
                Log.d("TSP_UI", "🛣️ 경로 그리기 완료: ${coordinates.size}개 점")
            } else {
                Log.w("TSP_UI", "⚠️ 경로 좌표가 비어있음")
            }
        } ?: run {
            Log.w("TSP_UI", "⚠️ 경로 데이터가 없음")
        }
    }

    private fun clearMapMarkersAndRoute() {
        destinationMarker?.remove()
        routePolyline?.remove()
        destinationMarker = null
        routePolyline = null

        // 현재 위치 마커는 유지
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}