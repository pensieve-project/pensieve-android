package ru.hse.pensieve.posts.models

import java.time.Instant
import java.util.UUID

data class Post(
    val themeId: UUID? = null,
    val authorId: UUID? = null,
    val postId: UUID? = null,
    val text: String? = null,
    val timeStamp: Instant? = null,
    val likesCount: Int? = null
)