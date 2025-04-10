package ru.hse.pensieve.posts.models

import java.util.UUID

data class CommentRequest (
    val postId: UUID? = null,
    val authorId: UUID? = null,
    val text: String? = null
)
