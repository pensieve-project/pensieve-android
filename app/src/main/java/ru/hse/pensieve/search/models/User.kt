package ru.hse.pensieve.search.models

import java.util.UUID

data class User(
    val userId: UUID? = null,
    val username: String? = null
)