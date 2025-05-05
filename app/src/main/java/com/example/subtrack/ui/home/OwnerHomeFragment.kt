package com.example.subtrack.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.subtrack.R
import com.example.subtrack.data.model.HomeData
import com.example.subtrack.databinding.FragmentOwnerHomeBinding
import com.example.subtrack.util.DateUtil
import com.example.subtrack.util.TokenManager
import com.example.subtrack.viewmodel.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class OwnerHomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentOwnerHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    private var googleMap: GoogleMap? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        Log.d("OwnerHomeFragment", "onViewCreated started")
        
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        // 구글 맵 설정
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
            Log.d("OwnerHomeFragment", "Map fragment found and getMapAsync called")
        } else {
            Log.e("OwnerHomeFragment", "Map fragment not found")
        }
        
        setupObservers()
        setupClickListeners()
        
        // 데이터 로드
        Log.d("OwnerHomeFragment", "Loading home data")
        viewModel.loadHomeData()
    }
    
    private fun setupClickListeners() {
        binding.fabAIChat.setOnClickListener {
            Toast.makeText(requireContext(), "AI 상담 기능은 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupObservers() {
        viewModel.homeData.observe(viewLifecycleOwner) { homeData ->
            Log.d("OwnerHomeFragment", "homeData observed: $homeData")
            updateUI(homeData)
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("OwnerHomeFragment", "Error: $errorMessage")
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("OwnerHomeFragment", "isLoading: $isLoading")
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
            }
        }
        
        viewModel.authError.observe(viewLifecycleOwner) { isAuthError ->
            if (isAuthError) {
                Log.e("OwnerHomeFragment", "Auth error occurred")
                Toast.makeText(requireContext(), "인증이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_LONG).show()
                // TokenManager에서 토큰 삭제
                TokenManager(requireContext()).clearToken()
                // 로그인 화면으로 이동
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }
    
    private fun updateUI(homeData: HomeData) {
        Log.d("OwnerHomeFragment", "updateUI called with data: $homeData")
        
        with(binding) {
            // 가게 정보 설정
            val shopAddress = buildString {
                append(homeData.store.address)
                append(" ")
                append(homeData.store.detailAddress)
            }
            Log.d("OwnerHomeFragment", "Setting shop address: $shopAddress")
            tvShopAddress.text = shopAddress
            
            // 기사 정보 설정
            Log.d("OwnerHomeFragment", "Setting driver info: ${homeData.assignedDriver.name}")
            tvCourierName.text = homeData.assignedDriver.name
            tvCourierPhone.text = homeData.assignedDriver.phoneNumber
            tvCourierCar.text = homeData.assignedDriver.vehicleNumber
            
            // 수거 날짜 설정
            val formattedDate = DateUtil.formatDateWithDay(homeData.pickupDate)
            Log.d("OwnerHomeFragment", "Setting pickup date: $formattedDate")
            tvPickupDateValue.text = formattedDate
            
            // 포인트 설정
            Log.d("OwnerHomeFragment", "Setting points: ${homeData.points}")
            tvPointValue.text = homeData.points.toString()
            
            // 구독 설정
            Log.d("OwnerHomeFragment", "Setting subscription: ${homeData.subscriptionName}")
            tvSub.text = homeData.subscriptionName
            
            // 지도 업데이트
            updateMapLocation(homeData.store.latitude, homeData.store.longitude, homeData.store.address)
        }
    }
    
    private fun updateMapLocation(latitude: Double, longitude: Double, title: String) {
        Log.d("OwnerHomeFragment", "Updating map location: lat=$latitude, lng=$longitude")
        googleMap?.let { map ->
            val location = LatLng(latitude, longitude)
            map.clear()
            map.addMarker(MarkerOptions().position(location).title(title))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
        } ?: Log.e("OwnerHomeFragment", "Google Map is null")
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.d("OwnerHomeFragment", "Google Map is ready")
        
        // 기본 위치 설정 (서울)
        val defaultLocation = LatLng(37.5665, 126.9780)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        
        // 이미 데이터가 로드되어 있다면 지도 업데이트
        viewModel.homeData.value?.let { homeData ->
            updateMapLocation(
                homeData.store.latitude,
                homeData.store.longitude,
                homeData.store.address
            )
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
