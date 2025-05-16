package com.please.data.repositories

import android.util.Log
import com.please.data.models.seller.DeliveryBaseResponse
import com.please.data.models.seller.DeliveryInfo
import com.please.data.models.seller.DeliveryStatus
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryRepository @Inject constructor() {
    
    // 실제 구현에서는 API 또는 로컬 데이터베이스에서 데이터를 관리합니다.
    // 지금은 메모리에 임시 저장합니다.
    private val deliveryList = mutableListOf<DeliveryInfo>()
    
    init {
        // 샘플 데이터 추가
        val calendar = Calendar.getInstance()
        
        // 내일 날짜 가져오기
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)

        // 기존 deliveryList는 새로 등록한 내역만 기입

        // 샘플 데이터: 내일 날짜의 택배 몇 개
        /*
        deliveryList.add(
            DeliveryInfo(
                id = UUID.randomUUID().toString(),
                productName = "노트북",
                recipientName = "홍길동",
                recipientPhone = "010-1234-5678",
                address = "서울시 강남구 테헤란로 123",
                detailAddress = "456호",
                pickupDate = tomorrow.time,
                packageSize = PackageSize.MEDIUM,
                status = DeliveryStatus.PENDING,
                trackingNumber = "1234567890"
            )
        )
         */
    }
    
    // 특정 날짜의 모든 배송 정보 가져오기
    fun getDeliveriesByDate(date: Date): List<DeliveryInfo> {
        val calendar = Calendar.getInstance().apply { time = date }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.time

        // 이것. deliverList가 List. 해당 기록에 대한 것.
        // 필터 날짜 List만 반환.
        return deliveryList.filter { 
            it.pickupDate.time >= startDate.time && it.pickupDate.time <= endDate.time 
        }
    }
    
    // 새 배송 정보 추가
    fun addDelivery(deliveryInfo: DeliveryInfo) {
        // 운송장 번호가 없으면 임의로 생성
        val infoWithTrackingNumber = if (deliveryInfo.trackingNumber.isNullOrEmpty()) {
            val trackingNumber = generateTrackingNumber()
            deliveryInfo.copy(trackingNumber = trackingNumber)
        } else {
            deliveryInfo
        }
        
        deliveryList.add(infoWithTrackingNumber)
    }
    
    // 운송장 번호 생성 (실제로는 배송 회사 API 등을 통해 발급)
    private fun generateTrackingNumber(): String {
        // 간단한 10자리 숫자 생성
        return (1000000000 + (Math.random() * 9000000000).toInt()).toString()
    }
    
    // 배송 정보 삭제
    fun deleteDelivery(id: String) {
        deliveryList.removeIf { it.id == id }
    }
    
    // 배송 상태 업데이트
    fun updateDeliveryStatus(id: String, status: DeliveryStatus) {
        val index = deliveryList.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = deliveryList[index].copy(status = status)
            deliveryList[index] = updated
        }
    }

    fun jsonDelivery(json: DeliveryBaseResponse): List<DeliveryInfo>{
        val deliveryList = mutableListOf<DeliveryInfo>()
        
        //조회한 모든 내역 변경 - 백엔드 반환값을 프론트 deliveryinfo 형식으로 반환
        val list = json.data
        for(pack in list) {
            val unit = DeliveryInfo(
                id = UUID.randomUUID().toString(), // 사용 (프론트 고유 변수)
                productName = pack.productName,
                recipientName = pack.recipientName,
                recipientPhone = "010-1234-5678", // TODO 미사용 - json 양식에 미기입 상태. 필요시 백엔드 수정
                address = pack.recipientAddr,
                detailAddress = pack.detailAddress,
                pickupDate = dateFromStringFormat(pack.pickupScheduledDate),
                packageSize = pack.size,
                status = convertStatus(pack.status),
                trackingNumber = pack.trackingCode
            )
            Log.d("Delivery/List_ADD", unit.trackingNumber.toString())
            deliveryList.add(unit)
        }

        return deliveryList
    }

    fun dateFromStringFormat(pack: String): Date{
        val dateString = pack // "2025-05-25T04:36:32.968Z"
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC") // Z는 UTC 의미

        val date: Date = formatter.parse(dateString)!!
        //val zonedDateTime = ZonedDateTime.parse(dateString)
        //val date: Date = Date.from(zonedDateTime.toInstant())
        //val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        return date
    }

    // 현재 데이터 상 분류가 총 6개로 나뉨...
    // 해당 분류를 프론트 기존 형식으로 임의 분류
    fun convertStatus(dataStatus: String): DeliveryStatus{
        /*
            PENDING_PICKUP       // 수거 전
            IN_PICKUP            // 수거 중
            PICKUP_COMPLETED     // 수거 완료
            PENDING_DELIVERY     // 배송 전
            IN_DELIVERY          // 배송 중
            DELIVERY_COMPLETED   // 배송 완료
        */
        if(dataStatus == "PENDING_PICKUP" || dataStatus == "IN_PICKUP") return DeliveryStatus.PENDING
        if(dataStatus == "PICKUP_COMPLETED" || dataStatus == "PENDING_DELIVERY" || dataStatus == "IN_DELIVERY" ) return DeliveryStatus.IN_PROGRESS
        if(dataStatus == "DELIVERY_COMPLETED") return DeliveryStatus.DELIVERED

        return DeliveryStatus.PENDING
    }
}
