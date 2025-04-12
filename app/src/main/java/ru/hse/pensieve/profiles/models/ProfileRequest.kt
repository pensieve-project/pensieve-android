package ru.hse.pensieve.profiles.models

import java.util.UUID

data class ProfileRequest(
    val authorId: UUID? = null,
    val avatar: ByteArray? = null,
    val description: String? = null
)