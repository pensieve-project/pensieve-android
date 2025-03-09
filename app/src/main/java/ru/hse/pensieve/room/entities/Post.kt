package ru.hse.pensieve.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: Int,
    val themeId: UUID,
    val authorId: UUID,
    val postId: UUID,
    val text: String,
//    val timeStamp: Instant,
    var likesCount: Int,
    // image
    // commentsCount
    // coAuthors
    // location
)