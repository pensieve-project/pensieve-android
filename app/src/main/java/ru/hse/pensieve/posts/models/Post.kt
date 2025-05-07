package ru.hse.pensieve.posts.models

import java.time.Instant
import java.util.UUID

data class Post(
    val themeId: UUID? = null,
    val authorId: UUID? = null,
    val postId: UUID? = null,
    val photo: ByteArray? = null,
    val text: String? = null,
    val timeStamp: Instant? = null,
    val location: Point? = null,
    val coAuthors: Set<UUID>? = null,
    val albumId: UUID? = null,
    val likesCount: Int? = null,
    val commentsCount: Int? = null
)