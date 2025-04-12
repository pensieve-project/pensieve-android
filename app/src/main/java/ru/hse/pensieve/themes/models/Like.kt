package ru.hse.pensieve.themes.models

import java.util.UUID

data class Like (
    val authorId: UUID? = null,
    val themeId: UUID? = null
)
