package com.please.data.repositories

import com.please.data.models.seller.DeliveryInfo
import com.please.data.models.seller.DeliveryStatus
import com.please.data.models.seller.PackageSize
import java.util.Calendar
import java.util.Date
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
        
        // 샘플 데이터: 내일 날짜의 택배 몇 개
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
        
        deliveryList.add(
            DeliveryInfo(
                id = UUID.randomUUID().toString(),
                productName = "의류",
                recipientName = "김철수",
                recipientPhone = "010-9876-5432",
                address = "부산시 해운대구 우동",
                pickupDate = tomorrow.time,
                packageSize = PackageSize.SMALL,
                status = DeliveryStatus.IN_PROGRESS,
                trackingNumber = "9876543210"
            )
        )
        
        deliveryList.add(
            DeliveryInfo(
                id = UUID.randomUUID().toString(),
                productName = "가구",
                recipientName = "박영희",
                recipientPhone = "010-5555-4444",
                address = "인천시 연수구 송도동",
                pickupDate = tomorrow.time,
                packageSize = PackageSize.LARGE,
                status = DeliveryStatus.IN_PROGRESS,
                isCautionRequired = true,
                trackingNumber = "5432167890"
            )
        )
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
}
