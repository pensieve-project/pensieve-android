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
import java.io.File
import java.util.UUID


class PostRepository {
    private val postApi = Client.getInstanceOfService(PostService::class.java)
    private val themesApi = Client.getInstanceOfService(ThemeService::class.java)
    private val userId: UUID? = UUID.fromString("a4956bff-2362-4ac7-88c1-fc4fdee8810f")

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
        return postApi.getPostsByAuthor(authorId).await()
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