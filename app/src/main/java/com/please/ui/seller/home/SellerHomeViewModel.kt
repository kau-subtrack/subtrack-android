package com.please.ui.seller.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.please.data.models.seller.SellerHomeInfo
import com.please.data.repositories.SellerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerHomeViewModel @Inject constructor(
    private val repository: SellerRepository
) : ViewModel() {

    private val _homeInfoState = MutableLiveData<HomeInfoState>()
    val homeInfoState: LiveData<HomeInfoState> = _homeInfoState

    init {
        loadHomeInfo()
    }

    private fun loadHomeInfo() {
        _homeInfoState.value = HomeInfoState.Loading
        
        viewModelScope.launch {
            try {
                val homeInfo = repository.getSellerHomeInfo()
                _homeInfoState.value = HomeInfoState.Success(homeInfo)
            } catch (e: Exception) {
                _homeInfoState.value = HomeInfoState.Error("정보를 불러오는데 실패했습니다: ${e.message}")
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
