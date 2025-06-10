package ru.hse.pensieve.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.hse.pensieve.posts.models.Point
import java.time.Instant
import java.util.UUID

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val postId: UUID,
    val themeId: UUID,
    val themeTitle : String,
    val authorId: UUID,
    val photo: ByteArray?,
    val text: String?,
    val timeStamp: Instant,
    val location: Point?,
    val coAuthors: Set<UUID>?,
    val albumId: UUID?,
    var likesCount: Int,
    var commentsCount: Int,
    var isLiked: Boolean,
    val lastAccessTime: Long = System.currentTimeMillis()
)