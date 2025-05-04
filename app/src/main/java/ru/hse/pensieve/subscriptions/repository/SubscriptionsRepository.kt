package ru.hse.pensieve.subscriptions.repository

import kotlinx.coroutines.Deferred
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.models.Like
import ru.hse.pensieve.subscriptions.models.SubscriptionRequest
import ru.hse.pensieve.subscriptions.route.SubscriptionsService
import ru.hse.pensieve.themes.route.ThemeService
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.models.ThemeRequest
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
        return subscriptionsService.getSubscriptions(targetId).await()
    }

    suspend fun getSubscribersCount(targetId: UUID): Int {
        return subscriptionsService.getSubscriptionsCount(targetId).await()
    }

    suspend fun subscribe(subscriberId: UUID, targetId: UUID): Boolean {
        return subscriptionsService.subscribe(SubscriptionRequest(subscriberId, targetId)).await().isSuccessful
    }

    suspend fun unsubscribe(subscriberId: UUID, targetId: UUID): Boolean {
        return subscriptionsService.unsubscribe(SubscriptionRequest(subscriberId, targetId)).await().isSuccessful
    }

    suspend fun hasUserSubscribed(subscriberId: UUID, targetId: UUID): Boolean {
        return subscriptionsService.hasUserSubscribed(subscriberId, targetId).await()
    }
}