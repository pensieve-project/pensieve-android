package ru.hse.pensieve.posts.models

import java.util.UUID

data class PostRequest(
    val text: String?,
    val authorId: UUID?,
    val threadId: UUID?
)