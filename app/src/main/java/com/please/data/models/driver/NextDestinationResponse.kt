package com.please.data.models.driver

import com.google.gson.annotations.SerializedName

data class NextDestinationResponse(
    val status: String,
    val message: String? = null,
    @SerializedName("next_destination")
    val nextDestination: NextDestination? = null,
    val route: RouteResponse? = null,
    @SerializedName("remaining_pickups")
    val remainingPickups: Int = 0,
    @SerializedName("remaining") // 🔧 배달용 필드 추가
    val remaining: Int = 0,
    @SerializedName("is_last")
    val isLast: Boolean = false
)

data class NextDestination(
    val lat: Double,
    val lon: Double,

    // 🔧 수거용 필드 (DriverMapFragment에서 사용)
    @SerializedName("parcel_id")
    val parcelId: String? = null,

    // 🔧 배달용 필드 (DriverDeliveryMapFragment에서 사용)
    @SerializedName("delivery_id")
    val deliveryId: String? = null,

    val name: String,
    val address: String,

    // 🔧 배달 API에서 추가로 오는 필드들
    @SerializedName("product_name")
    val productName: String? = null,

    @SerializedName("recipient_name")
    val recipientName: String? = null,

    @SerializedName("recipient_phone")
    val recipientPhone: String? = null,

    @SerializedName("location_name")
    val locationName: String? = null
) {
    // 🔧 수거용 ID 가져오기 (수거 Fragment에서 사용)
    fun getPickupId(): String? = parcelId

    // 🔧 배달용 ID 가져오기 (배달 Fragment에서 사용) - 유연하게 처리
    fun getValidDeliveryId(): String? {
        return deliveryId ?: parcelId  // deliveryId가 없으면 parcelId 사용
    }

    // 🔧 실제 사용할 ID를 반환하는 헬퍼 프로퍼티 (호환성 유지)
    val actualId: String?
        get() = parcelId ?: deliveryId

    // 🔧 실제 카운트를 반환하는 헬퍼 프로퍼티
    fun getActualRemaining(response: NextDestinationResponse): Int {
        return if (response.remainingPickups > 0) response.remainingPickups else response.remaining
    }
}

data class RouteResponse(
    val coordinates: List<Coordinate>? = null,
    val waypoints: List<Waypoint>? = null,
    val trip: RouteTrip? = null
)

data class Coordinate(
    val lat: Double,
    val lon: Double
)

data class Waypoint(
    val lat: Double,
    val lon: Double,
    val name: String,
    val instruction: String
)

data class RouteTrip(
    val summary: RouteSummary? = null
)

data class RouteSummary(
    val time: Double = 0.0,
    val length: Double = 0.0
)