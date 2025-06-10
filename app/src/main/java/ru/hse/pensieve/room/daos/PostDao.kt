package ru.hse.pensieve.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.hse.pensieve.room.entities.PostEntity
import java.util.UUID

@Dao
interface PostDao {
    @Insert
    suspend fun insert(post: PostEntity)

    @Query("SELECT * FROM posts WHERE postId = :postId")
    suspend fun getPostById(postId: UUID): PostEntity?

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<PostEntity>

    @Query("SELECT photo FROM posts WHERE postId = :postId")
    suspend fun getPostPhoto(postId: UUID): ByteArray

    @Query("UPDATE posts SET likesCount = :newLikesCount WHERE postId = :postId")
    suspend fun updateLikesCount(postId: UUID, newLikesCount: Int)

    @Query("UPDATE posts SET commentsCount = :newCommentsCount WHERE postId = :postId")
    suspend fun updateCommentsCount(postId: UUID, newCommentsCount: Int)

    @Query("UPDATE posts SET isLiked = :newIsLiked WHERE postId = :postId")
    suspend fun updateIsLiked(postId: UUID, newIsLiked: Boolean)

    @Query("UPDATE posts SET lastAccessTime = :time WHERE postId = :postId")
    suspend fun updateLastAccessTime(postId: UUID, time : Long)

    @Query("DELETE FROM posts WHERE postId NOT IN " +
            "(SELECT postId FROM posts ORDER BY lastAccessTime DESC LIMIT 50)")
    suspend fun keepLatestPosts()
}