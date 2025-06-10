package ru.hse.pensieve.room.repositories

import android.app.Application
import ru.hse.pensieve.room.AppDatabase
import ru.hse.pensieve.room.daos.PostDao
import ru.hse.pensieve.room.entities.PostEntity
import java.util.UUID
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postDao: PostDao
) {
    suspend fun insertPost(post: PostEntity) {
        postDao.insert(post)
    }

    suspend fun getPostById(postId: UUID): PostEntity? {
        return postDao.getPostById(postId)
    }

    suspend fun getPostPhoto(postId : UUID) : ByteArray? {
        return postDao.getPostPhoto(postId)
    }

    suspend fun updateLikesCount(postId : UUID, newLikesCount : Int) {
        postDao.updateLikesCount(postId, newLikesCount)
    }

    suspend fun updateCommentsCount(postId : UUID, newCommentsCount : Int) {
        postDao.updateCommentsCount(postId, newCommentsCount)
    }

    suspend fun updateIsLiked(postId : UUID, newIsLiked : Boolean) {
        postDao.updateIsLiked(postId, newIsLiked)
    }

    suspend fun updateLastAccessTime(postId : UUID, time : Long) {
        postDao.updateLastAccessTime(postId, time)
    }
}