package ru.hse.pensieve.subscriptions.route

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.hse.pensieve.subscriptions.models.SubscriptionRequest
import java.util.UUID

interface SubscriptionsService {
    @GET("/subscriptions/subscriptions")
    fun getSubscriptions(@Query("subscriberId") subscriberId: UUID): Deferred<List<UUID>>

    @GET("/subscriptions/subscriptions-count")
    fun getSubscriptionsCount(@Query("subscriberId") subscriberId: UUID): Deferred<Int>

    @GET("/subscriptions/subscribers")
    fun getSubscribers(@Query("targetId") targetId: UUID): Deferred<List<UUID>>

    @GET("/subscriptions/subscribers-count")
    fun getSubscribersCount(@Query("targetId") targetId: UUID): Deferred<Int>

    @POST("/subscriptions/subscribe")
    fun subscribe(@Body request: SubscriptionRequest): Deferred<Response<Void>>

    @POST("/subscriptions/unsubscribe")
    fun unsubscribe(@Body request: SubscriptionRequest): Deferred<Response<Void>>

    @GET("/subscriptions/subscribed")
    fun hasUserSubscribed(@Query("subscriberId") subscriberId: UUID, @Query("targetId") targetId: UUID): Deferred<Boolean>
}
