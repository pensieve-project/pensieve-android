package ru.hse.pensieve.posts.models

import java.util.UUID

data class Comment (
    val postId: UUID? = null,
    val commentId: UUID? = null,
    val authorId: UUID? = null,
    val text: String? = null
)
