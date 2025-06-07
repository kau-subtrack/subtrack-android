package com.please.di

import com.google.gson.*
import com.please.data.models.driver.ParcelSize
import java.lang.reflect.Type

/**
 * ParcelSize enum을 JSON과 변환하는 Gson 어댑터
 */
class ParcelSizeAdapter : JsonSerializer<ParcelSize>, JsonDeserializer<ParcelSize> {

    // 객체를 JSON으로 직렬화
    override fun serialize(src: ParcelSize, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.name)
    }

    // JSON을 객체로 역직렬화
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ParcelSize {
        // JSON 값이 문자열인 경우
        if (json.isJsonPrimitive && json.asJsonPrimitive.isString) {
            val sizeName = json.asString
            try {
                // 정확히 enum 이름과 일치하는 경우
                return ParcelSize.valueOf(sizeName.uppercase())
            } catch (e: IllegalArgumentException) {
                // 이름이 일치하지 않는 경우 매핑 시도
                when (sizeName.lowercase()) {
                    "small", "소", "s" -> return ParcelSize.SMALL
                    "medium", "중", "m" -> return ParcelSize.MEDIUM
                    "large", "대", "l" -> return ParcelSize.LARGE
                    "xlarge", "x-large", "특대", "xl" -> return ParcelSize.XLARGE
                    else -> return ParcelSize.MEDIUM // 기본값
                }
            }
        } 
        // 숫자로 수신된 경우 (인덱스 기반 매핑)
        else if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber) {
            return when(json.asInt) {
                0 -> ParcelSize.SMALL
                1 -> ParcelSize.MEDIUM
                2 -> ParcelSize.LARGE
                3 -> ParcelSize.XLARGE
                else -> ParcelSize.MEDIUM
            }
        }
        
        // 기본값 반환
        return ParcelSize.MEDIUM
    }
}
