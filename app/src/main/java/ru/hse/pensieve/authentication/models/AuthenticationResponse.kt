package ru.hse.pensieve.authentication.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AuthenticationResponse(
    @JsonProperty("id") val id: UUID,
    @JsonProperty("username") val username: String,
    @JsonProperty("accessToken") val accessToken: String,
    @JsonProperty("refreshToken") val refreshToken: String,
) {
    constructor() : this(UUID.randomUUID(), "", "", "")
}