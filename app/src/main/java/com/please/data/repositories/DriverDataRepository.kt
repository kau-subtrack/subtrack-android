package com.please.data.repositories

import com.please.data.models.driver.CollectionRequest
import com.please.data.models.driver.DeliveryRequest

/**
 * 배송기사의 수거 및 배송 요청 데이터를 관리하는 싱글톤 클래스
 */
object DriverDataRepository {
    
    // 수거 요청 목록
    private val collectionRequests = mutableListOf(
        CollectionRequest(
            id = "1",
            trackingNumber = "1234567890",
            productType = "의류",
            productDetails = "의류, 신발"
        ),
        CollectionRequest(
            id = "2",
            trackingNumber = "1234567890",
            productType = "전자제품",
            productDetails = "전자제품, 도서"
        ),
        CollectionRequest(
            id = "3",
            trackingNumber = "1234235232",
            productType = "가구",
            productDetails = "가구, 주방용품"
        )
    )
    
    // 배송 요청 목록
    private val deliveryRequests = mutableListOf(
        DeliveryRequest(
            id = "1",
            trackingNumber = "1234567890",
            productDetails = "의류, 신발"
        ),
        DeliveryRequest(
            id = "2",
            trackingNumber = "3456789012",
            productDetails = "운동화, 가방"
        ),
        DeliveryRequest(
            id = "3",
            trackingNumber = "5678901234",
            productDetails = "코트, 모자"
        )
    )
    
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
        collectionRequests.addAll(
            listOf(
                CollectionRequest(
                    id = "1",
                    trackingNumber = "1234567890",
                    productType = "의류",
                    productDetails = "의류, 신발"
                ),
                CollectionRequest(
                    id = "2",
                    trackingNumber = "1234567890",
                    productType = "전자제품",
                    productDetails = "전자제품, 도서"
                ),
                CollectionRequest(
                    id = "3",
                    trackingNumber = "1234235232",
                    productType = "가구",
                    productDetails = "가구, 주방용품"
                )
            )
        )
        
        deliveryRequests.clear()
        deliveryRequests.addAll(
            listOf(
                DeliveryRequest(
                    id = "1",
                    trackingNumber = "1234567890",
                    productDetails = "의류, 신발"
                ),
                DeliveryRequest(
                    id = "2",
                    trackingNumber = "3456789012",
                    productDetails = "운동화, 가방"
                ),
                DeliveryRequest(
                    id = "3",
                    trackingNumber = "5678901234",
                    productDetails = "코트, 모자"
                )
            )
        )
    }
}
