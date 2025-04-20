package com.example.subtrack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.subtrack.data.model.SendItem

class SendViewModel : ViewModel() {

    private val _registerList = MutableLiveData<List<SendItem>>()
    val registerList: LiveData<List<SendItem>> = _registerList

    private val _queryList = MutableLiveData<List<SendItem>>()
    val queryList: LiveData<List<SendItem>> = _queryList

    init {
        // 예시 데이터
        _registerList.value = listOf(
            SendItem("1234567890", "나이키 운동화", "010-1234-5678", "서울 강남구 테헤란로 123")
        )

        _queryList.value = listOf(
            SendItem("1234567890", status = "배송완료"),
            SendItem("9876543210", status = "배송중"),
            SendItem("5432167890", status = "배송중")
        )
    }
}
