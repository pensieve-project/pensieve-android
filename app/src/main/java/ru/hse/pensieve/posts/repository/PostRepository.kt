package ru.hse.pensieve.posts.repository

import ru.hse.pensieve.api.Client
import ru.hse.pensieve.posts.route.PostApiService
import ru.hse.pensieve.posts.models.Post
import ru.hse.pensieve.posts.models.PostRequest
import java.util.UUID

class PostRepository {
    private val postApi = Client.getInstanceOfService(PostApiService::class.java)
    private val userId: UUID? = UUID.fromString("a4956bff-2362-4ac7-88c1-fc4fdee8810f")

    suspend fun createPost(text: String, themeId: UUID): Post {
        return postApi.createPost(PostRequest(text, userId, themeId)).await()
    }

    suspend fun getPostsByAuthor(authorId: UUID): List<Post> {
        return postApi.getPostsByAuthor(authorId).await()
    }

    suspend fun getPostsByTheme(themeId: UUID): List<Post> {
        return postApi.getPostsByTheme(themeId).await()
    }
}