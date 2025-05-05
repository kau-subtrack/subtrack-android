package com.example.subtrack.data.model

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    val status: Boolean,
    val data: HomeData?,
    val message: String?
)

data class HomeData(
    val store: StoreInfo,
    val assignedDriver: DriverInfo,
    val pickupDate: String,
    val points: Int,
    val subscriptionName: String
)

data class StoreInfo(
    val address: String,
    val detailAddress: String,
    val latitude: Double,
    val longitude: Double
)

data class DriverInfo(
    val name: String,
    val phoneNumber: String,
    val vehicleNumber: String
)
