package com.example.subtrack.data.model

data class SendItem(
    val trackingNumber: String,
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val status: String = ""  // 배송중, 배송완료 등
    //val date: String = ""  yyyy-MM-dd 형식의 수거일
)