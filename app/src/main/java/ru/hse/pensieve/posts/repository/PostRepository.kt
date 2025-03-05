package ru.hse.pensieve.posts.repository

import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.route.PostService
import ru.hse.pensieve.themes.route.ThemeService
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.posts.models.PostRequest
import ru.hse.pensieve.themes.models.Theme
import ru.hse.pensieve.themes.models.ThemeRequest
import java.util.UUID

class PostRepository {
    private val postApi = Client.getInstanceOfService(PostService::class.java)
    private val themesApi = Client.getInstanceOfService(ThemeService::class.java)
    private val userId: UUID? = UUID.fromString("a4956bff-2362-4ac7-88c1-fc4fdee8810f")

    suspend fun createPostInExistingTheme(text: String, themeId: UUID): Post {
        return postApi.createPost(PostRequest(text, userId, themeId)).await()
    }

    suspend fun createPostInNewTheme(text: String, themeTitle: String): Post {
        val theme = themesApi.createTheme(ThemeRequest(userId, themeTitle)).await()
        return postApi.createPost(PostRequest(text, userId, theme.themeId)).await()
    }

    suspend fun getPostsByAuthor(authorId: UUID): List<Post> {
        return postApi.getPostsByAuthor(authorId).await()
    }

    suspend fun getPostsByTheme(themeId: UUID): List<Post> {
        return postApi.getPostsByTheme(themeId).await()
    }
}