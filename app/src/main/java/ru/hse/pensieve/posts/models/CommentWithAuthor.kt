package ru.hse.pensieve.posts.models

data class CommentWithAuthor(
    val comment: Comment,
    val authorUsername: String,
    val authorPhoto: ByteArray?
)