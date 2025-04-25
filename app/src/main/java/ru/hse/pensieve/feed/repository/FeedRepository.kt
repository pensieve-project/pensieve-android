package ru.hse.pensieve.feed.repository

import kotlinx.coroutines.Deferred
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.feed.route.FeedService
import ru.hse.pensieve.posts.models.Like
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.subscriptions.models.SubscriptionRequest
import ru.hse.pensieve.subscriptions.route.SubscriptionsService
import ru.hse.pensieve.themes.route.ThemeService
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.models.ThemeRequest
import ru.hse.pensieve.utils.UserPreferences
import java.time.Instant
import java.util.UUID

class FeedRepository {
    private val feedService = Client.getInstanceOfService(FeedService::class.java)
    private val userId: UUID? = UserPreferences.getUserId()

    suspend fun getSubscriptionsFeed(limit: Int, lastSeenTime: Instant): List<Post> {
        val result = feedService.getSubscriptionsFeed(userId!!, limit, lastSeenTime).await()
        println("Received " + result.size + " posts in feed repo")
        return result
    }
}