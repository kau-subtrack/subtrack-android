package com.please.data.models.driver

data class DeliveryComRequest(
    val deliveryId: Int
)

//error의 경우?
data class DeliveryComResponse(
    val status: String
)


