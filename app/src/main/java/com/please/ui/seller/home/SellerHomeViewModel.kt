package com.please.ui.seller.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.R
import com.please.data.models.seller.SellerHomeInfo
import com.please.data.repositories.AuthRepository
import com.please.data.repositories.GoogleMapRepository
import com.please.data.repositories.LocationResult
import com.please.data.repositories.SellerRepository
import com.please.ui.auth.login.LoginViewModel.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerHomeViewModel @Inject constructor(
    private val repository: SellerRepository,
    private val repository_map: GoogleMapRepository
) : ViewModel() {

    private val _homeInfoState = MutableLiveData<HomeInfoState>()
    val homeInfoState: LiveData<HomeInfoState> = _homeInfoState

    private val _locationResult = MutableLiveData<LocationResult>()
    val locationResult: LiveData<LocationResult> = _locationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    init {
        val token = AuthRepository.AppState.userToken ?: "none"
        loadHomeInfo(token)
    }

    private fun loadHomeInfo(token: String) {
        if(token == "none") Log.d("HOME/CALL/ERROR", "none")

        _homeInfoState.value = HomeInfoState.Loading

        viewModelScope.launch {
            Log.d("HOME/CALL" , token)
            try {
                //시도. 매개변수 필요. token
                val response = repository.ownerHome(token)
                Log.d("HOME/CALL" , token)
                Log.d("HOME/CALL" , response.body().toString())

                //성공시, res body반환.
                if (response.isSuccessful && response.body() != null) {
                    _homeInfoState.value = HomeInfoState.Success(response.body()!!)
                } else {
                    _homeInfoState.value = HomeInfoState.Error("홈화면 조회에 실패했습니다: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.d("HOME/CALL" , e.message.toString())
                _homeInfoState.value = HomeInfoState.Error("홈화면 조회에 실패했습니다: ${e.message}")
            }
        }

    }

    //지도 로드
    fun loadMaps(address: String) {
        Log.d("MAP/CALL" , address)
        viewModelScope.launch {
            try {
                //getString(R.string.google_maps_key)
                val list = repository_map.searchAddress(address)
                Log.d("MAP/CALL" , list.toString())
                _locationResult.value = list
            } catch (e: Exception) {
                // 예외 처리
            }
        }
    }

    // 홈 정보 상태를 나타내는 sealed class
    sealed class HomeInfoState {
        object Loading : HomeInfoState()
        data class Success(val data: SellerHomeInfo) : HomeInfoState()
        data class Error(val message: String) : HomeInfoState()
    }
}