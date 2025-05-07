package ru.hse.pensieve.albums.models

import java.util.UUID

data class Album (
    val userId: UUID? = null,
    val coAuthors: Set<UUID>? = null,
    val albumId: UUID? = null,
    val avatar: ByteArray? = null
)