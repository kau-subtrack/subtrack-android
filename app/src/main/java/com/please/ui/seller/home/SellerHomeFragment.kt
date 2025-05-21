package com.please.ui.seller.home

import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.please.R
import com.please.data.repositories.LocationResult
import com.please.databinding.FragmentSellerHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerHomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSellerHomeBinding? = null
    private val binding get() = _binding!!

    private var mMap: GoogleMap? = null
    private val defaultSeoulLocation = LatLng(37.5665, 126.9780) //지도 기본 위치 - 서울특별시

    private val viewModel: SellerHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        //홈 화면 내역 로딩
        viewModel.homeInfoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SellerHomeViewModel.HomeInfoState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }
                is SellerHomeViewModel.HomeInfoState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    //이중 data
                    val data = state.data.data
                    val storeLocation = data.store.address + " " + data.store.detailAddress

                    // 가게 위치 정보 설정
                    binding.tvStoreAddress.text = storeLocation //data.data.storeInfo storeInfo.address

                    // 지도 설정
                    //val address = "서울특별시" //하드코딩
                    viewModel.loadMaps(storeLocation)

                    // 수거 날짜 설정
                    binding.tvPickupDate.text = data.pickupDate // data.pickupInfo.date
                    
                    // 배정 기사 정보 설정
                    binding.tvCourierName.text = "1"//data.courierInfo.name
                    binding.tvCourierPhone.text = "2" //data.courierInfo.phoneNumber
                    binding.tvCourierCar.text = "3" //data.courierInfo.carNumber
                    
                    // 포인트 정보 설정
                    binding.tvPoints.text = data.points.toString()
                    
                    // 구독 정보 설정
                    binding.tvSubscription.text = data.subscriptionName

                }
                is SellerHomeViewModel.HomeInfoState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        //지도 화면 로딩
        viewModel.locationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LocationResult.Success -> {
                    updateMapLocation(result.latLng, result.address)
                }
                is LocationResult.Error -> {
                    Toast.makeText(context, "${result.message}\n주소가 잘못 되었습니다.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun setupListeners() {
        // 챗봇 버튼 클릭 이벤트
        binding.fabChatbot.setOnClickListener {
            // 챗봇 화면으로 이동
            findNavController().navigate(R.id.action_sellerHomeFragment_to_chatbotFragment)
        }
    }
    
    //기본 지도 로딩
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultSeoulLocation, 15f))
        /*
        mMap?.addMarker(MarkerOptions()
            .position(defaultSeoulLocation)
            .title("현재 위치")
            .snippet("서울특별시"))
         */
    }

    //위치 변경 및 마커 추가
    private fun updateMapLocation(latLng: LatLng, address: String) {
        mMap?.clear()
        mMap?.addMarker(MarkerOptions().position(latLng).title(address))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
