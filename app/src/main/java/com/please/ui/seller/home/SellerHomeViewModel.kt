package com.please.ui.seller.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.seller.SellerHomeInfo
import com.please.data.repositories.AuthRepository
import com.please.data.repositories.SellerRepository
import com.please.ui.auth.login.LoginViewModel.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerHomeViewModel @Inject constructor(
    private val repository: SellerRepository
) : ViewModel() {

    private val _homeInfoState = MutableLiveData<HomeInfoState>()
    val homeInfoState: LiveData<HomeInfoState> = _homeInfoState

    //fragement를 만들때 시행되어야하지 않음? 이게 문제라고 보는데.
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

    // 홈 정보 상태를 나타내는 sealed class
    sealed class HomeInfoState {
        object Loading : HomeInfoState()
        data class Success(val data: SellerHomeInfo) : HomeInfoState()
        data class Error(val message: String) : HomeInfoState()
    }
}
