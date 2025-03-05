package ru.hse.pensieve.themes.models

import java.time.Instant
import java.util.UUID

data class Theme(
    val themeId: UUID? = null,
    val authorId: UUID? = null,
    val title: String? = null,
    val timeStamp: Instant? = null
)