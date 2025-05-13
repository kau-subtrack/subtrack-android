package com.please.data.models.driver

/**
 * 배송 요청 정보를 담는 데이터 클래스
 */
data class DeliveryRequest(
    val id: String,              // 배송 요청 ID
    val trackingNumber: String,  // 송장번호
    val productDetails: String   // 제품 상세 정보
)
