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
data class RegisterDelivery(
    val productName: String,
    val recipientName: String,
    val recipientPhone: String,
    val recipientAddr: String, // 배송 주소
    val detailAddress: String? = null, // 상세 주소
    val size: PackageSize,
    val caution: Boolean,
    val pickupScheduledDate: String, // 수거 날짜
    //val isCautionRequired: Boolean = false, // 취급주의 여부
    //val trackingNumber: String? = null
)

data class RegisterDeliveryResponse(
    val status: Boolean,
    val message: String,
    val parcelId: Int, // 기입된 고유 번호. 쓸일이 있을진 모르겠네.
    val usedPoints: Int
)

data class ComTrackPack(
    val trackingCode: String,
    val recipientName: String,
    val recipientAddr: String,
    val detailAddress: String,
    val productName: String,
    val status: String, //enum 이면 좋은데... 스키마 양식이 바뀜
    val deliveryCompletedAt: Date, //반환 문제가 있다면, String으로 바꾸고, viewmodel에서 formatDate 함수로 변경반환
    val pickupScheduledDate: Date,
    val size: PackageSize
)

data class DeliveryBaseResponse(
    val status: Boolean,
    val date: String,
    val data: List<ComTrackPack>
)