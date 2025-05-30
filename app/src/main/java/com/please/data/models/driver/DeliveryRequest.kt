package com.please.data.models.driver

/**
 * 배송 요청 정보를 담는 데이터 클래스
 */
data class DeliveryRequest(
    val id: Int,                // 배송 요청 ID
    val trackingNumber: String, // 송장번호
    val productDetails: String  // 제품 상세 정보
)

//
data class DeliveryResponse(
    val status: Boolean,
    val data: List<DeliveryList>
)

data class DeliveryList(
    val trackingCode: String,
    val deliveryAddress: Address,
    val deliveryTimeWindow: String,
    val status: String,
    val isNextDeliveryTarget: Boolean
)

data class Address(
    val address: String,
    val detailAddress: String
)

//
data class TrackCodeRequest(
    val trackingCode: String
)

data class DeliveryComResponse(
    val status: Boolean,
    val data: DeliveryCom
)

data class DeliveryCom(
    val trackingCode: String,
    val productName: String,
    val deliveryAddress: Address,
    val deliveryTimeWindow: String,
    val status: Boolean
)

//이제 DB와 일치하도록 Int 타입으로 수정
data class id(
    val ownerId: Int
)

