package ru.hse.pensieve.subscriptions.repository

import ru.hse.pensieve.api.Client
import ru.hse.pensieve.subscriptions.models.SubscriptionRequest
import ru.hse.pensieve.subscriptions.route.SubscriptionsService
import ru.hse.pensieve.utils.UserPreferences
import java.util.UUID

class SubscriptionsRepository {
    private val subscriptionsService = Client.getInstanceOfService(SubscriptionsService::class.java)
    private val userId: UUID? = UserPreferences.getUserId()

    suspend fun getSubscriptions(subscriberId: UUID): List<UUID> {
        return subscriptionsService.getSubscriptions(subscriberId).await()
    }

    suspend fun getSubscriptionsCount(subscriberId: UUID): Int {
        return subscriptionsService.getSubscriptionsCount(subscriberId).await()
    }

    suspend fun getSubscribers(targetId: UUID): List<UUID> {
        return subscriptionsService.getSubscribers(targetId).await()
    }

    suspend fun getSubscribersCount(targetId: UUID): Int {
        return subscriptionsService.getSubscribersCount(targetId).await()
    }

    suspend fun subscribe(subscriberId: UUID, targetId: UUID): Boolean {
        return subscriptionsService.subscribe(SubscriptionRequest(subscriberId, targetId)).await().isSuccessful
    }

    suspend fun unsubscribe(subscriberId: UUID, targetId: UUID): Boolean {
        return subscriptionsService.unsubscribe(subscriberId, targetId).await().isSuccessful
    }

    suspend fun hasUserSubscribed(subscriberId: UUID, targetId: UUID): Boolean {
        return subscriptionsService.hasUserSubscribed(subscriberId, targetId).await()
    }
}