package ru.hse.pensieve.posts.models

import java.util.UUID

data class Like (
    val authorId: UUID? = null,
    val postId: UUID? = null
)
