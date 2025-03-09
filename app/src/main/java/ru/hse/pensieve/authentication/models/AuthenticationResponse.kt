package ru.hse.pensieve.authentication.models

import java.util.UUID

data class AuthenticationResponse(
    val id: UUID? = null,
    val username: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
)