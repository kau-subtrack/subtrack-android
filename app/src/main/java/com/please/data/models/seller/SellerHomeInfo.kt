package com.please.data.models.seller

data class StoreInfo(
    val address: String,
    val latitude: Double,
    val longitude: Double
)

data class CourierInfo(
    val name: String,
    val phoneNumber: String,
    val carNumber: String
)

data class PickupInfo(
    val date: String
)

data class SellerHomeInfo(
    val storeInfo: StoreInfo,
    val pickupInfo: PickupInfo,
    val courierInfo: CourierInfo,
    val points: Int,
    val subscription: String
)
