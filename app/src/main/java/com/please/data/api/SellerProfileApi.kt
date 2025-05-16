package com.please.data.api

import com.please.data.models.AddressUpdateRequest
import com.please.data.models.ApiResponse
import com.please.data.models.PasswordUpdateRequest
import com.please.data.models.SellerProfile
import com.please.data.models.seller.DeliveryBaseResponse
import com.please.data.models.seller.SellerHomeInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.QueryMap

interface SellerProfileApi {

    //home
    @GET("owner/home")
    suspend fun ownerHome(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>

    //shipment
    @GET("owner/shipment-history/completed") //월간 내역에 대한 조회 기능. - 아래 리스트랑은 월간조회이냐 일간조회이냐 차이뿐임.
    suspend fun shipmentCompleted(@Header("authorization") authorization: String, @QueryMap params: Map<String, String>): Response<DeliveryBaseResponse>

    @GET("owner/shipment/list")
    suspend fun shipmentList(@Header("authorization") authorization: String, @QueryMap params: Map<String, String>): Response<DeliveryBaseResponse>
/*
    @GET("owner/shipment/trackingNumber")
    suspend fun shipmentTrack(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>

    @POST("owner/shipment/register")
    suspend fun shipmentRegister(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>

    //points
    @POST("owner/points/subscribe")
    suspend fun subscribe(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>

    @POST("owner/points/charge")
    suspend fun charge(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>

    @GET("owner/points/history")
    suspend fun history(@Header("authorization") authorization: String): Response<SellerHomeInfo> //Response<ApiResponse<SellerHomeInfo>>


     */

    // 마이페이지
    //router.post('/my-page', updateStoreInfo); // 가게 정보 수정하기
    //router.post('/my-page/change-password', changePassword); // 비밀번호 수정하기


    //none
    @GET("owner/profile")
    suspend fun getSellerProfile(): Response<ApiResponse<SellerProfile>>

    //미사용
    @PUT("owner/address")
    suspend fun updateAddress(@Body request: AddressUpdateRequest): Response<ApiResponse<Unit>>

    //미사용
    @PUT("owner/password")
    suspend fun updatePassword(@Body request: PasswordUpdateRequest): Response<ApiResponse<Unit>>
}
