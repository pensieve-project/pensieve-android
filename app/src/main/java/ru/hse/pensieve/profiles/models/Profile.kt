package ru.hse.pensieve.profiles.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Profile(
    @JsonProperty("authorId")
    val authorId: UUID? = null,

    @JsonProperty("avatar")
    val avatar: ByteArray? = null,

    @JsonProperty("description")
    val description: String? = null,

    @JsonProperty("likedThemesIds")
    val likedThemesIds: MutableList<UUID>? = null,

    @JsonProperty("likedPostsIds")
    val likedPostsIds: MutableList<UUID>? = null,

    @JsonProperty("subscriptionsCount")
    val subscriptionsCount: Int? = null,

    @JsonProperty("subscribersCount")
    val subscribersCount: Int? = null,

    @JsonProperty("isVip")
    val isVip: Boolean? = null
)