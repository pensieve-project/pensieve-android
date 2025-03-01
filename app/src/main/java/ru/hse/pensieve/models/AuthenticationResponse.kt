package ru.hse.pensieve.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AuthenticationResponse(
    @JsonProperty("id") val id: UUID,
    @JsonProperty("username") val username: String,
) {
    constructor() : this(UUID.randomUUID(), "")
}