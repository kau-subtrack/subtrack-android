package com.please.data.repositories

import com.please.data.api.SellerProfileApi
import com.please.data.models.seller.SellerHomeInfo
import javax.inject.Inject
import retrofit2.Response
//import okhttp3.Response

//@Singleton
class SellerRepository @Inject constructor(
    private val ownerService: SellerProfileApi
) {
    suspend fun ownerHome(token: String): Response<SellerHomeInfo> {
        return ownerService.ownerHome("Bearer $token")
    }
        /*
        class header(
            authorization: String = "Bearer $token"
        )

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

         */
}
