package com.example.subtrack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.subtrack.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var addressInput: EditText
    private lateinit var searchButton: Button

    // 기본 위치 설정 (서울특별시)
    private val defaultSeoulLocation = LatLng(37.5665, 126.9780)
    
    // 현재 위치 (서울특별시 송파구 잠실동)
    private val currentJamsilLocation = LatLng(37.5134, 127.1001)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        addressInput = view.findViewById(R.id.addressInput)
        searchButton = view.findViewById(R.id.searchButton)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        searchButton.setOnClickListener {
            val address = addressInput.text.toString()
            if (address.isNotEmpty()) {
                // TODO: Replace with your actual Google Maps API key
                val apiKey = getString(R.string.google_maps_key)
                viewModel.searchAddress(address, apiKey)
            } else {
                // 주소가 입력되지 않은 경우 서울특별시 기본 좌표로 이동
                updateMapLocation(defaultSeoulLocation, "서울특별시")
                Toast.makeText(context, "주소가 입력되지 않아 서울특별시로 이동합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.locationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LocationResult.Success -> {
                    updateMapLocation(result.latLng, result.address)
                }
                is LocationResult.Error -> {
                    // 에러 발생 시 서울특별시 기본 좌표로 이동
                    updateMapLocation(defaultSeoulLocation, "서울특별시")
                    Toast.makeText(context, "${result.message}\n서울특별시로 이동합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            searchButton.isEnabled = !isLoading
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        
        // 지도가 준비되면 현재 위치(잠실동)로 이동
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentJamsilLocation, 15f))
        mMap?.addMarker(MarkerOptions()
            .position(currentJamsilLocation)
            .title("현재 위치")
            .snippet("서울특별시 송파구 잠실동"))
    }

    private fun updateMapLocation(latLng: LatLng, address: String) {
        mMap?.clear()
        mMap?.addMarker(MarkerOptions().position(latLng).title(address))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}
