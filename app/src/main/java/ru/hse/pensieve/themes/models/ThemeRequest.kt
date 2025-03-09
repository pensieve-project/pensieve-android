package ru.hse.pensieve.themes.models

import java.util.UUID

data class ThemeRequest(
    val authorId: UUID?,
    val title: String?
)