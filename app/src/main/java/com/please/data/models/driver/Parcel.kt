package com.please.data.models.driver

import java.util.Date

/**
 * 소포 정보를 담는 데이터 클래스
 * DB 스키마의 Parcel 모델과 일치
 */
data class Parcel(
    val id: Int,                     // 고유 ID
    val ownerId: Int,                // 발송자 유저 ID
    val pickupDriverId: Int?,        // 수거 기사 ID
    val deliveryDriverId: Int?,      // 배송 기사 ID
    val isNextPickupTarget: Boolean, // 다음 수거 대상 여부
    val isNextDeliveryTarget: Boolean, // 다음 배송 대상 여부
    val isDeleted: Boolean,          // OWNER 삭제 요청 여부
    val productName: String,         // 제품명
    val size: ParcelSize,            // 택배 크기
    val caution: Boolean,            // 파손주의 여부
    val recipientName: String,       // 수령인 이름
    val recipientPhone: String,      // 수령인 전화번호
    val recipientAddr: String,       // 수령인 주소
    val detailAddress: String?,      // 수령인 상세 주소
    val trackingCode: String?,       // 운송장 번호
    val status: ParcelStatus,        // 수거/배송 상태
    val pickupScheduledDate: Date?,  // 수거 예정일
    val deliveryScheduledDate: Date?, // 배송 예정일
    val pickupCompletedAt: Date?,    // 수거 완료 시각
    val deliveryCompletedAt: Date?,  // 배송 완료 시각
    val createdAt: Date,             // 소포 등록 시각
    val pickupTimeWindow: String?,   // 수거 시간대 표시
    val deliveryTimeWindow: String?, // 배송 시간대 표시
    val deliveryImageUrl: String     // 배송 완료 인증 사진 URL
)
