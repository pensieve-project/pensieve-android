package ru.hse.pensieve.repositories

import android.app.Application
import ru.hse.pensieve.room.AppDatabase
import ru.hse.pensieve.room.entities.Post
import java.util.UUID

class PostRepository(application: Application) {
    private val postDao = AppDatabase.getInstance(application).postDao()

    suspend fun insertPost(post: Post) {
        postDao.insert(post)
    }

    suspend fun getPostById(postId: UUID): Post? {
        return postDao.getPostById(postId)
    }

    suspend fun getAllPosts(): List<Post> {
        return postDao.getAllPosts()
    }
}