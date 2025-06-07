package com.please.data.models.driver

/**
 * 소포 상태를 나타내는 열거형
 * DB 스키마의 ParcelStatus enum과 일치
 */
enum class ParcelStatus {
    PICKUP_PENDING,       // 수거 전
    PICKUP_COMPLETED,     // 수거 완료
    DELIVERY_PENDING,     // 배송 전
    DELIVERY_COMPLETED    // 배송 완료
}
