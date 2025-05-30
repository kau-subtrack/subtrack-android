package com.please.data.models.driver

data class AiDeliveryComRequest(
    val deliveryId: Int
)

//error의 경우?
data class AiDeliveryComResponse(
    val status: String
)


