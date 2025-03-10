package ru.hse.pensieve.posts.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.posts.route.PostService
import ru.hse.pensieve.themes.models.ThemeRequest
import ru.hse.pensieve.themes.route.ThemeService
import ru.hse.pensieve.utils.UserPreferences
import java.io.File
import java.util.UUID


class PostRepository {
    private val postApi = Client.getInstanceOfService(PostService::class.java)
    private val themesApi = Client.getInstanceOfService(ThemeService::class.java)
    private val userId: UUID = UserPreferences.getUserId()!!

    suspend fun createPostInExistingTheme(text: String, photo: File, themeId: UUID): Post {
        val filePart = MultipartBody.Part.createFormData(
            "photo", photo.getName(), RequestBody.create(
                MediaType.parse("image/*"), photo
            )
        )
        val textPart = RequestBody.create(MediaType.parse("text/plain"), text)
        val userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId.toString())
        val themeIdPart = RequestBody.create(MediaType.parse("text/plain"), themeId.toString())
        return postApi.createPost(textPart, filePart, userIdPart, themeIdPart).await()
    }

    suspend fun createPostInNewTheme(text: String, photo: File, themeTitle: String): Post {
        val filePart = MultipartBody.Part.createFormData(
            "photo", photo.getName(), RequestBody.create(
                MediaType.parse("image/*"), photo
            )
        )
        println(filePart)
        val textPart = RequestBody.create(MediaType.parse("text/plain"), text)
        val userIdPart = RequestBody.create(MediaType.parse("text/plain"), userId.toString())
        return withContext(Dispatchers.IO) {
            val theme = themesApi.createTheme(ThemeRequest(userId, themeTitle)).await()
            val themeIdPart = RequestBody.create(MediaType.parse("text/plain"), theme.themeId.toString())
            postApi.createPost(textPart, filePart, userIdPart, themeIdPart).await()
        }
    }

    suspend fun getPostsByAuthor(authorId: UUID): List<Post> {
        return try {
            println("Sending request for authorId: $authorId")
            val response = postApi.getPostsByAuthor(authorId).await()
            println("Response received: $response")
            response
        } catch (e: Exception) {
            println("Error in getPostsByAuthor: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPostsByTheme(themeId: UUID): List<Post> {
        return postApi.getPostsByTheme(themeId).await()
    }


    private fun createPartFromString(value: String?): RequestBody? {
        return value?.let {
            RequestBody.create(MediaType.parse("text/plain"), it)
        }
    }
}