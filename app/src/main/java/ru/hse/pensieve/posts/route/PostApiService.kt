package ru.hse.pensieve.posts.route

import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.posts.models.PostRequest
import java.util.UUID

interface PostApiService {
    @POST("/posts")
    fun createPost(@Body request: PostRequest): Deferred<Post>

    @GET("/by-author")
    fun getPostsByAuthor(authorId: UUID?): Deferred<List<Post>>

    @GET("/by-thread")
    fun getPostsByThread(threadId: UUID?): Deferred<List<Post>>
}
