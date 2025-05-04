package com.please.data.repositories

import com.please.data.api.SubscriptionApi
import com.please.data.models.SubscriptionResponse
import javax.inject.Inject

class SubscriptionRepository @Inject constructor(
    private val subscriptionApi: SubscriptionApi
) {
    // 구독 신청
    suspend fun subscribeToPlan(planId: Int): Result<SubscriptionResponse> {
        return try {
            val response = subscriptionApi.subscribeToPlan(mapOf("planId" to planId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("구독 신청에 실패했습니다: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
