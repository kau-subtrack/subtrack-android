package com.please.data.models.driver

/**
 * 수거 요청 정보를 담는 데이터 클래스
 */
data class CollectionRequest(
    val id: String,            // 수거 요청 ID
    val trackingNumber: String, // 운송장 번호
    val productType: String,    // 제품 종류
    val productDetails: String  // 제품 상세 정보
)
