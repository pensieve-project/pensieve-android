package ru.hse.pensieve.posts.repository

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.models.Comment
import ru.hse.pensieve.posts.models.CommentRequest
import ru.hse.pensieve.posts.models.Like
import ru.hse.pensieve.posts.models.LocationDto
import ru.hse.pensieve.posts.models.Point
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.posts.route.PostService
import ru.hse.pensieve.themes.models.ThemeRequest
import ru.hse.pensieve.themes.route.ThemeService
import ru.hse.pensieve.utils.UserPreferences
import java.io.File
import java.util.UUID


class PostRepository {
    private val postService = Client.getInstanceOfService(PostService::class.java)
    private val themesService = Client.getInstanceOfService(ThemeService::class.java)
    private val userId: UUID = UserPreferences.getUserId()!!

    suspend fun createPostInExistingTheme(text: String, photo: File, themeId: UUID, location: Point, coAuthors: Set<UUID>): Post {
        val filePart = MultipartBody.Part.createFormData(
            "photo", photo.getName(), RequestBody.create(
                MediaType.parse("image/*"), photo
            )
        )
        val textPart = RequestBody.create(MediaType.parse("text/plain"), text)
        val locationDto = LocationDto(location.latitude, location.longitude)
        val locationJson = Gson().toJson(locationDto)
        val locationPart = MultipartBody.Part.createFormData(
            "location",
            null,
            RequestBody.create(
               MediaType.parse("application/json"), locationJson
            )
        )
        val userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId.toString())
        val themeIdPart = RequestBody.create(MediaType.parse("text/plain"), themeId.toString())
        val coAuthorParts = coAuthors.map { coAuthorId ->
            MultipartBody.Part.createFormData(
                "coAuthors",
                null,
                RequestBody.create(
                    MediaType.parse("text/plain"), coAuthorId.toString()
                )
            )
        }
        return postService.createPost(textPart, filePart, locationPart, userIdPart, themeIdPart, coAuthorParts).await()
    }

    suspend fun createPostInNewTheme(text: String, photo: File, themeTitle: String, location: Point, coAuthors: Set<UUID>): Post {
        val filePart = MultipartBody.Part.createFormData(
            "photo", photo.getName(), RequestBody.create(
                MediaType.parse("image/*"), photo
            )
        )
        println(filePart)
        val textPart = RequestBody.create(MediaType.parse("text/plain"), text)
        val locationDto = LocationDto(location.latitude, location.longitude)
        val locationJson = Gson().toJson(locationDto)
        val locationPart = MultipartBody.Part.createFormData(
            "location",
            null,
            RequestBody.create(
                MediaType.parse("application/json"), locationJson
            )
        )
        val userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId.toString())
        val coAuthorParts = coAuthors.map { coAuthorId ->
            MultipartBody.Part.createFormData(
                "coAuthors",
                null,
                RequestBody.create(
                    MediaType.parse("text/plain"), coAuthorId.toString()
                )
            )
        }
        return withContext(Dispatchers.IO) {
            val theme = themesService.createTheme(ThemeRequest(userId, themeTitle)).await()
            val themeIdPart = RequestBody.create(MediaType.parse("text/plain"), theme.themeId.toString())
            postService.createPost(textPart, filePart, locationPart, userIdPart, themeIdPart, coAuthorParts).await()
        }
    }

    suspend fun getPostsByAuthor(authorId: UUID): List<Post> {
        return try {
            println("Sending request for authorId: $authorId")
            val response = postService.getPostsByAuthor(authorId).await()
            println("Response received: $response")
            response
        } catch (e: Exception) {
            println("Error in getPostsByAuthor: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAllPosts(): List<Post> {
        return postService.getAllPosts().await()
    }

    suspend fun getPostsByTheme(themeId: UUID): List<Post> {
        return postService.getPostsByTheme(themeId).await()
    }

    suspend fun getPostById(postId: UUID): Post {
        return postService.getPostById(postId).await()
    }

    suspend fun likePost(authorId: UUID, postId: UUID): Boolean {
        return postService.likePost(Like(authorId, postId)).await().isSuccessful
    }

    suspend fun unlikePost(authorId: UUID, postId: UUID): Boolean {
        return postService.unlikePost(authorId, postId).await().isSuccessful
    }

    suspend fun hasUserLikedPost(authorId: UUID, postId: UUID) : Boolean {
        return postService.hasUserLikedPost(authorId, postId).await()
    }

    suspend fun getLikesCount(postId: UUID) : Int {
        return postService.getLikesCount(postId).await()
    }

    suspend fun leaveComment(postId: UUID, authorId: UUID, text: String) : Comment {
        return postService.leaveComment(CommentRequest(postId, authorId, text)).await()
    }

    suspend fun getPostComments(postId: UUID) : List<Comment> {
        return postService.getPostComments(postId).await()
    }

    suspend fun getCommentsCount(postId: UUID) : Int {
        return postService.getCommentsCount(postId).await()
    }

}