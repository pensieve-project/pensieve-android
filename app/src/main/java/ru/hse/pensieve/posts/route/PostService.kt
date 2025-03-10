package ru.hse.pensieve.posts.route

import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.room.entities.User
import ru.hse.pensieve.themes.route.ThemeService
import java.util.UUID

interface PostService {
    @Multipart
    @POST("/posts")
    fun createPost(@Part("text") text: RequestBody,
                   @Part photo: MultipartBody.Part,
                   @Part("authorId") authorId: RequestBody,
                   @Part("themeId") themeId: RequestBody
    ): Deferred<Post>

    @GET("/posts/by-author")
    fun getPostsByAuthor(@Query("authorId") authorId: UUID): Deferred<List<Post>>

    @GET("posts/by-theme")
    fun getPostsByTheme(themeId: UUID?): Deferred<List<Post>>
}
