package ru.hse.pensieve.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Tokens(
    @JsonProperty("accessToken") val accessToken: String? = null,
    @JsonProperty("refreshToken") val refreshToken: String? = null
) {
}