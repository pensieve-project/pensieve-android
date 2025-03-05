package ru.hse.pensieve.authentication.models.tokens

data class Tokens(
    val accessToken: String? = null,
    val refreshToken: String? = null
)
