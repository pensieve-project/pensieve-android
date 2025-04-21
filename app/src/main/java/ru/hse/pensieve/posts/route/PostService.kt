package ru.hse.pensieve.posts.route

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import ru.hse.pensieve.posts.models.Comment
import ru.hse.pensieve.posts.models.CommentRequest
import ru.hse.pensieve.posts.models.Like
import ru.hse.pensieve.posts.models.Post
import java.util.UUID

interface PostService {
    @Multipart
    @POST("/posts")
    fun createPost(@Part("text") text: RequestBody,
                   @Part photo: MultipartBody.Part,
                   @Part("location") location: RequestBody,
                   @Part("authorId") authorId: RequestBody,
                   @Part("themeId") themeId: RequestBody
    ): Deferred<Post>

    @GET("/posts")
    fun getAllPosts(): Deferred<List<Post>>

    @GET("/posts/by-author")
    fun getPostsByAuthor(@Query("authorId") authorId: UUID): Deferred<List<Post>>

    @GET("/posts/by-theme")
    fun getPostsByTheme(@Query("themeId") themeId: UUID?): Deferred<List<Post>>

    @GET("/posts/by-id")
    fun getPostById(@Query("postId") postId: UUID?): Deferred<Post>

    @POST("/posts/like")
    fun likePost(@Body request: Like?): Deferred<Response<Void>>

    @POST("/posts/unlike")
    fun unlikePost(@Body request: Like?): Deferred<Response<Void>>

    @GET("/posts/liked")
    fun hasUserLikedPost(@Query("authorId") authorId: UUID?, @Query("postId") postId: UUID?): Deferred<Boolean>

    @GET("/posts/likes-count")
    fun getLikesCount(@Query("postId") postId: UUID?): Deferred<Int>

    @POST("/posts/comment")
    fun leaveComment(@Body request: CommentRequest?): Deferred<Comment>

    @GET("/posts/comments")
    fun getPostComments(@Query("postId") postId: UUID?): Deferred<List<Comment>>

    @GET("/posts/comments-count")
    fun getCommentsCount(@Query("postId") postId: UUID?): Deferred<Int>
}
