package ru.hse.pensieve.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.hse.pensieve.room.entities.Post
import ru.hse.pensieve.room.entities.User
import java.util.UUID

@Dao
interface PostDao {
    @Insert
    suspend fun insert(post: Post)

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: UUID): Post?

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<Post>
}