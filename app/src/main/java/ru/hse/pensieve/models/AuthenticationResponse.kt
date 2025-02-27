package ru.hse.pensieve.models

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthenticationResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("username") val username: String
) {
    constructor() : this(0, "")
}