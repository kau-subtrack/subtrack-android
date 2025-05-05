package com.example.subtrack.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subtrack.network.RetrofitClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _locationResult = MutableLiveData<LocationResult>()
    val locationResult: LiveData<LocationResult> = _locationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun searchAddress(address: String, apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.geocodingService.getLatLngFromAddress(address, apiKey)
                
                if (response.status == "OK" && response.results.isNotEmpty()) {
                    val location = response.results[0].geometry.location
                    val latLng = LatLng(location.lat, location.lng)
                    _locationResult.value = LocationResult.Success(latLng, response.results[0].formatted_address)
                } else {
                    _locationResult.value = LocationResult.Error("주소를 찾을 수 없습니다.")
                }
            } catch (e: Exception) {
                _locationResult.value = LocationResult.Error("오류가 발생했습니다: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

sealed class LocationResult {
    data class Success(val latLng: LatLng, val address: String) : LocationResult()
    data class Error(val message: String) : LocationResult()
}
