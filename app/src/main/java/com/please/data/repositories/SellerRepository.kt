package com.please.data.repositories

import com.please.data.api.SellerProfileApi
import com.please.data.models.seller.DeliveryBaseResponse
import com.please.data.models.seller.RegisterDelivery
import com.please.data.models.seller.RegisterDeliveryResponse
import com.please.data.models.seller.SellerHomeInfo
import javax.inject.Inject
import retrofit2.Response
import java.util.Date
import java.util.Calendar

//import okhttp3.Response

//@Singleton
class SellerRepository @Inject constructor(
    private val ownerService: SellerProfileApi
) {

    suspend fun ownerHome(token: String): Response<SellerHomeInfo> {
        return ownerService.ownerHome("Bearer $token")
    }

    suspend fun shipmentCompleted(token: String, date: Date): Response<DeliveryBaseResponse>{
        val calendar = Calendar.getInstance().apply { time = date }
        val params = mapOf(
            "year" to calendar.get(Calendar.YEAR).toString(),
            "month" to (calendar.get(Calendar.MONTH) + 1).toString(), //왠지는 모르겠지만 월만 -1임.
            "day" to calendar.get(Calendar.DATE).toString() // 안 쓰이는 파라미터.
        )

        return ownerService.shipmentCompleted("Bearer $token", params)
    }

    suspend fun shipmentDateView(token: String, date: Date): Response<DeliveryBaseResponse>{
        val calendar = Calendar.getInstance().apply { time = date }
        val params = mapOf(
            "year" to calendar.get(Calendar.YEAR).toString(),
            "month" to (calendar.get(Calendar.MONTH) + 1).toString(), //왠지는 모르겠지만 월만 -1임.
            "day" to calendar.get(Calendar.DATE).toString()
        )
        //Log.d("Delivery/TRY_DATE", params.toString())

        return ownerService.shipmentList("Bearer $token", params)
    }

    suspend fun registerShipment(token: String, delivery: RegisterDelivery): Response<RegisterDeliveryResponse>{
        return ownerService.shipmentRegister("Bearer $token", delivery)
    }





    /*
    suspend fun login(id: String, password: String, userType: UserType): Response<LoginResponse> {
        return apiService.login(LoginRequest(id, password, userType))
    }

    suspend fun register(registerRequest: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(registerRequest)
    }

    suspend fun checkIdDuplicate(id: String): Response<Boolean> {
        return apiService.checkIdDuplicate(id)
    }
    */
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
