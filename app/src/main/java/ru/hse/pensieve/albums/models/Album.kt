package ru.hse.pensieve.albums.models

import java.time.Instant
import java.util.UUID

data class Album (
    val userId: UUID? = null,
    val coAuthors: Set<UUID>? = null,
    val timeStamp: Instant? = null
)