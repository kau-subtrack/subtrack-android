package com.please.data.models.driver

data class PickResponse(
    val status: Boolean,
    val data: List<PickList>
)

data class PickList(
    val ownerId: Int,
    val address: String,
    val detailAddress: String,
    val pickupTimeWindow: String,
    val productName: String,
    val parcelCount: Int,
    val status: String, //해당 내용은 enum이긴 하나.. 애매함.
    val isNextPickupTarget: Boolean
)

data class IdRequest(
    val ownerId: Int
)