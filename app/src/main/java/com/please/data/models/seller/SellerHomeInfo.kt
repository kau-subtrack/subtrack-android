package com.please.data.models.seller

data class StoreInfo(
    val address: String,
    val detailAddress: String,
    val latitude: Double,
    val longitude: Double
)

data class Store(
    val store: StoreInfo,
    val assignedDriver: String? = "none",
    val pickupDate: String,
    val points: Int,
    val subscriptionName: String
)

data class SellerHomeInfo(
    val status: String,
    val data: Store
)


    /*
    tmp
    val storeInfo: StoreInfo,
    val pickupInfo: PickupInfo,
    val courierInfo: CourierInfo,
    val points: Int,
    val subscription: String

//미사용
data class CourierInfo(
    val name: String,
    val phoneNumber: String,
    val carNumber: String
)

//미사용
data class PickupInfo(
    val date: String
)

     */