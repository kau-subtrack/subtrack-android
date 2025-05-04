package com.please.data.models.seller

import java.util.Date

enum class DeliveryStatus {
    PENDING, // 배송전
    IN_PROGRESS, // 배송중
    DELIVERED // 배송완료
}

enum class PackageSize {
    SMALL,    // 소 (100cm / 5kg 이하)
    MEDIUM,   // 중 (120cm / 10kg 이하)
    LARGE,    // 대 (140cm / 20kg 이하)
    XLARGE    // 특대 (160cm / 30kg 이하)
}

data class DeliveryInfo(
    val id: String, // 고유 ID
    val productName: String, // 제품명
    val recipientName: String, // 수령인 이름
    val recipientPhone: String, // 수령인 전화번호
    val address: String, // 배송 주소
    val detailAddress: String? = null, // 상세 주소
    val pickupDate: Date, // 수거 날짜
    val status: DeliveryStatus = DeliveryStatus.PENDING, // 배송 상태
    val packageSize: PackageSize, // 택배 크기
    val isCautionRequired: Boolean = false, // 취급주의 여부
    val trackingNumber: String? = null // 운송장 번호 (시스템에서 자동 생성될 수 있음)
)
