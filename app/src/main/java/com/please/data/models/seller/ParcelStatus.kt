package com.please.data.models.seller

/**
 * 백엔드 DB와 일치하는 택배 상태 열거형
 * 백엔드에서 사용하는 상세 배송 상태
 */
enum class ParcelStatus {
    PENDING_PICKUP,       // 수거 전
    IN_PICKUP,            // 수거 중
    PICKUP_COMPLETED,     // 수거 완료
    PENDING_DELIVERY,     // 배송 전
    IN_DELIVERY,          // 배송 중
    DELIVERY_COMPLETED    // 배송 완료
}
