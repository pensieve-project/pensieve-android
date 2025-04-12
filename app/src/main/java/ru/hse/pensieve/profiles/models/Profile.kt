package ru.hse.pensieve.profiles.models

import java.util.UUID

data class Profile(
    val authorId: UUID? = null,
    val avatar: ByteArray? = null,
    val description: String? = null,
    val likedThemesIds: MutableList<UUID> = mutableListOf(),
    val likedPostsIds: MutableList<UUID> = mutableListOf()
)