package com.please.di

import com.google.gson.*
import com.please.data.models.driver.ParcelStatus
import java.lang.reflect.Type

/**
 * ParcelStatus enum을 JSON과 변환하는 Gson 어댑터
 */
class ParcelStatusAdapter : JsonSerializer<ParcelStatus>, JsonDeserializer<ParcelStatus> {

    // 객체를 JSON으로 직렬화
    override fun serialize(src: ParcelStatus, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.name)
    }

    // JSON을 객체로 역직렬화
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ParcelStatus {
        // JSON 값이 문자열인 경우
        if (json.isJsonPrimitive && json.asJsonPrimitive.isString) {
            val statusName = json.asString
            try {
                // 정확히 enum 이름과 일치하는 경우
                return ParcelStatus.valueOf(statusName.uppercase())
            } catch (e: IllegalArgumentException) {
                // 이름이 일치하지 않는 경우 상태 매핑 시도
                when (statusName.lowercase()) {
                    "pickup_pending", "pickup pending", "수거 전" -> return ParcelStatus.PICKUP_PENDING
                    "pickup_completed", "pickup completed", "수거 완료" -> return ParcelStatus.PICKUP_COMPLETED
                    "delivery_pending", "delivery pending", "배송 전" -> return ParcelStatus.DELIVERY_PENDING 
                    "delivery_completed", "delivery completed", "배송 완료" -> return ParcelStatus.DELIVERY_COMPLETED
                    else -> return ParcelStatus.PICKUP_PENDING // 기본값
                }
            }
        } 
        // boolean 값으로 수신된 경우 (호환성을 위해)
        else if (json.isJsonPrimitive && json.asJsonPrimitive.isBoolean) {
            return if (json.asBoolean) ParcelStatus.DELIVERY_COMPLETED else ParcelStatus.PICKUP_PENDING
        }
        // 숫자로 수신된 경우 (인덱스 기반 매핑)
        else if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber) {
            return when(json.asInt) {
                0 -> ParcelStatus.PICKUP_PENDING
                1 -> ParcelStatus.PICKUP_COMPLETED
                2 -> ParcelStatus.DELIVERY_PENDING
                3 -> ParcelStatus.DELIVERY_COMPLETED
                else -> ParcelStatus.PICKUP_PENDING
            }
        }
        
        // 기본값 반환
        return ParcelStatus.PICKUP_PENDING
    }
}
