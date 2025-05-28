package com.please.data.models.driver

//공통 response로 변경
data class HealthResponse(
    val status: String
)

//아래 전부 nextResponse에 대한것. 뭐를 사용하는것이 맞는가하면 ㅁ?ㄹ. 가독성상? 역순 배치
data class PathAiNextResponse(
    val status: String,
    val is_last: Boolean,
    val nextDestination: NextD,
    val remaining: Int,
    val route: Trip
)


data class Trip(
    val has_traffic: Boolean,
    val language: String,
    val legs: List<Legs>,
    val locations: List<Locations>,
    val status: Int,
    val status_message: String,
    val summery: Summery,
    val traffic_data_count: Int,
    val units: String
)

data class Legs(
    val maneuvers: List<Maneuvers>,
    val shape: String,
    val summery: Summery
)

data class Maneuvers(
    val begin_shape_index: Int,
    val cost: Double,
    val end_shape_index: Int,
    val instruction: String,
    val length: Double,
    val time: Double,
    val travel_mode: String,
    val travel_type: String,
    val type: Int
)


data class NextD(
    val lat: Double,
    val lng: Double,
    val name: String
)

data class Locations(
    val lat: Double,
    val lon: Double,
    val name: String,
    val original_index: Int,
    val type: String
)

//양식이 중복되나, 하나 누락되는것이 존재.
data class Summery(
    val cost: Double,
    val has_ferry: Boolean,
    val has_highway: Boolean,
    val has_time_restrictions: Boolean,
    val has_toll: Boolean,
    val length: Double,
    val max_lat: Double,
    val max_lon: Double,
    val min_lat: Double,
    val min_lon: Double,
    val time: Double
)
