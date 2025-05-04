package ru.hse.pensieve.feed.route

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.hse.pensieve.posts.models.Post
import java.time.Instant
import java.util.UUID

interface FeedService {
    @GET("/feed/subscriptions")
    fun getSubscriptionsFeed(@Query("userId") userId: UUID, @Query("limit") limit: Int, @Query("lastSeenTime") lastSeenTime: Instant): Deferred<List<Post>>

}
