package com.please.data.repositories

import com.please.data.models.driver.CollectionRequest
import com.please.data.models.driver.DeliveryRequest

/**
 * 배송기사의 수거 및 배송 요청 데이터를 관리하는 싱글톤 클래스
 */
object DriverDataRepository {
    
    // 수거 요청 목록
    private val collectionRequests = mutableListOf<CollectionRequest>().apply {
        // 30개의 테스트 데이터 생성
        for (i in 1..30) {
            add(
                CollectionRequest(
                    id = i.toString(),
                    trackingNumber = "12345${i.toString().padStart(5, '0')}",
                    productType = when (i % 5) {
                        0 -> "의류"
                        1 -> "전자제품"
                        2 -> "가구"
                        3 -> "식품"
                        else -> "화장품"
                    },
                    productDetails = when (i % 5) {
                        0 -> "의류, 신발"
                        1 -> "전자제품, 도서"
                        2 -> "가구, 주방용품"
                        3 -> "식품, 음료"
                        else -> "화장품, 미용용품"
                    }
                )
            )
        }
    }
    
    // 배송 요청 목록
    private val deliveryRequests = mutableListOf<DeliveryRequest>().apply {
        // 30개의 테스트 데이터 생성
        for (i in 1..30) {
            add(
                DeliveryRequest(
                    id = i.toString(),
                    trackingNumber = "98765${i.toString().padStart(5, '0')}",
                    productDetails = when (i % 5) {
                        0 -> "의류, 신발"
                        1 -> "운동화, 가방"
                        2 -> "코트, 모자"
                        3 -> "전자제품, 생활용품"
                        else -> "식품, 음료"
                    }
                )
            )
        }
    }
    
    // 수거 요청 목록 조회 (복사본 반환)
    fun getCollectionRequests(): List<CollectionRequest> {
        return collectionRequests.toList()
    }
    
    // 배송 요청 목록 조회 (복사본 반환)
    fun getDeliveryRequests(): List<DeliveryRequest> {
        return deliveryRequests.toList()
    }
    
    // 수거 요청 삭제
    fun removeCollectionRequest(id: String): Boolean {
        return collectionRequests.removeIf { it.id == id }
    }
    
    // 배송 요청 삭제
    fun removeDeliveryRequest(id: String): Boolean {
        return deliveryRequests.removeIf { it.id == id }
    }
    
    // 테스트용 데이터 초기화 함수 (필요시 사용)
    fun resetData() {
        collectionRequests.clear()
        deliveryRequests.clear()
        
        // 수거 요청 데이터 재설정 (30개)
        for (i in 1..30) {
            collectionRequests.add(
                CollectionRequest(
                    id = i.toString(),
                    trackingNumber = "12345${i.toString().padStart(5, '0')}",
                    productType = when (i % 5) {
                        0 -> "의류"
                        1 -> "전자제품"
                        2 -> "가구"
                        3 -> "식품"
                        else -> "화장품"
                    },
                    productDetails = when (i % 5) {
                        0 -> "의류, 신발"
                        1 -> "전자제품, 도서"
                        2 -> "가구, 주방용품"
                        3 -> "식품, 음료"
                        else -> "화장품, 미용용품"
                    }
                )
            )
        }
        
        // 배송 요청 데이터 재설정 (30개)
        for (i in 1..30) {
            deliveryRequests.add(
                DeliveryRequest(
                    id = i.toString(),
                    trackingNumber = "98765${i.toString().padStart(5, '0')}",
                    productDetails = when (i % 5) {
                        0 -> "의류, 신발"
                        1 -> "운동화, 가방"
                        2 -> "코트, 모자"
                        3 -> "전자제품, 생활용품"
                        else -> "식품, 음료"
                    }
                )
            )
        }
    }
}
