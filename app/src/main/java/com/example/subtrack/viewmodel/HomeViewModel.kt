package com.example.subtrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.subtrack.data.model.HomeData
import com.example.subtrack.data.repository.HomeRepository
import com.example.subtrack.util.TokenManager
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val tokenManager = TokenManager(application)
    private val repository = HomeRepository(tokenManager)
    
    private val _homeData = MutableLiveData<HomeData>()
    val homeData: LiveData<HomeData> = _homeData
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _authError = MutableLiveData<Boolean>()
    val authError: LiveData<Boolean> = _authError
    
    fun loadHomeData() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getHomeInfo().fold(
                onSuccess = { response ->
                    _isLoading.value = false
                    if (response.status && response.data != null) {
                        _homeData.value = response.data
                    } else {
                        _error.value = response.message ?: "알 수 없는 오류가 발생했습니다."
                    }
                },
                onFailure = { throwable ->
                    _isLoading.value = false
                    val errorMessage = throwable.message ?: "알 수 없는 오류가 발생했습니다."
                    
                    // 401 에러 체크
                    if (errorMessage.contains("401") || errorMessage.contains("OWNER 권한이 필요합니다")) {
                        _authError.value = true
                    } else {
                        _error.value = errorMessage
                    }
                }
            )
        }
    }
}
