package com.please.data.repositories

import com.please.data.models.seller.CourierInfo
import com.please.data.models.seller.PickupInfo
import com.please.data.models.seller.SellerHomeInfo
import com.please.data.models.seller.StoreInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SellerRepository @Inject constructor() {
    
    // 실제 구현에서는 API 또는 로컬 데이터베이스에서 데이터를 가져옵니다.
    // 지금은 목업 데이터를 반환합니다.
    suspend fun getSellerHomeInfo(): SellerHomeInfo {
        return SellerHomeInfo(
            storeInfo = StoreInfo(
                address = "서울특별시 강남구 테헤란로 123",
                latitude = 37.5087,
                longitude = 127.0632
            ),
            pickupInfo = PickupInfo(
                date = "2025년 4월 12일 (토)"
            ),
            courierInfo = CourierInfo(
                name = "이재혁",
                phoneNumber = "010-1234-5678",
                carNumber = "서울 12가 3456"
            ),
            points = 300,
            subscription = "Standard Plus"
        )
    }
}
