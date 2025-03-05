package ru.hse.pensieve.posts.models

import android.os.Build
import androidx.annotation.RequiresApi
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Getter
import lombok.Setter
import java.time.Instant
import java.util.UUID

@Getter
@Setter
class Post(
    @JsonProperty("themeId") themeId: UUID?,
    @JsonProperty("authorId") authorId: UUID?,
    @JsonProperty("postId") postId: UUID?,
    @JsonProperty("photo") photo: ByteArray?,
    @JsonProperty("text") text: String?,
    @JsonProperty("timeStamp") timeStamp: Instant?,
    @JsonProperty("likesCount") likesCount: Int?) {
    constructor() : this(null, null, null, null, null, null, null)
}